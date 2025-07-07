package com.bookinline.bookinline.integration.controller;

import com.bookinline.bookinline.dto.AuthenticationRequest;
import com.bookinline.bookinline.dto.BookingRequestDto;
import com.bookinline.bookinline.entity.Booking;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.BookingStatus;
import com.bookinline.bookinline.entity.enums.PropertyType;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.entity.enums.UserStatus;
import com.bookinline.bookinline.repository.BookingRepository;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class BookingControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private Flyway flyway;

    User host = new User();
    User guest = new User();
    Property property = new Property();
    Booking booking = new Booking();
    String guestToken;

    @BeforeEach
    public void setup() throws Exception {
        flyway.clean();
        flyway.migrate();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        host.setFullName("Jane Doe");
        host.setEmail("janedoe91@gmail.com");
        host.setPassword(passwordEncoder.encode("password456"));
        host.setPhoneNumber("0987654321");
        host.setRole(Role.HOST);
        host.setStatus(UserStatus.ACTIVE);
        host = userRepository.save(host);

        guest.setFullName("John Doe");
        guest.setEmail("johndoe88@gmail.com");
        guest.setPassword(passwordEncoder.encode("password123"));
        guest.setPhoneNumber("1234567890");
        guest.setRole(Role.GUEST);
        guest.setStatus(UserStatus.ACTIVE);
        guest = userRepository.save(guest);

        property.setTitle("Beautiful Beach House");
        property.setDescription("A beautiful beach house with stunning views.");
        property.setCity("Miami, FL");
        property.setFloorArea(250);
        property.setBedrooms(5);
        property.setPropertyType(PropertyType.HOUSE);
        property.setAddress("123 Beach Ave");
        property.setPricePerNight(new BigDecimal(250.0));
        property.setHost(host);
        property.setMaxGuests(6);
        property.setAvailable(true);
        property = propertyRepository.save(property);

        booking.setProperty(property);
        booking.setGuest(guest);
        booking.setCheckInDate(LocalDate.now());
        booking.setCheckOutDate(LocalDate.now().plusDays(3));
        booking.setStatus(BookingStatus.PENDING);
        booking = bookingRepository.save(booking);

        AuthenticationRequest authenticationRequest = new AuthenticationRequest("johndoe88@gmail.com", "password123");
        guestToken = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andReturn().getResponse().getContentAsString();
        guestToken = guestToken.substring(16, guestToken.length() - 2);
    }

    @Test
    void shouldSuccessfullyBookAProperty() throws Exception {
        BookingRequestDto requestDto = new BookingRequestDto(LocalDate.now().plusDays(3), LocalDate.now().plusDays(5));
        mockMvc.perform(post("/api/bookings/property/" + property.getId() + "/book")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + guestToken)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checkInDate").value(requestDto.getCheckInDate().toString()))
                .andExpect(jsonPath("$.checkOutDate").value(requestDto.getCheckOutDate().toString()))
                .andExpect(jsonPath("$.status").value(BookingStatus.PENDING.toString()));
    }

    @Test
    void shouldFailBooking() throws Exception {
        BookingRequestDto requestDto = new BookingRequestDto(LocalDate.now(), LocalDate.now().plusDays(3));
        mockMvc.perform(post("/api/bookings/property/" + property.getId() + "/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + guestToken)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Property is not available for this date range"));
    }

    @Test
    void shouldCancelBooking() throws Exception {
        mockMvc.perform(delete("/api/bookings/" + booking.getId() + "/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + guestToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void shouldFailCancelBooking() throws Exception {
        mockMvc.perform(delete("/api/bookings/" + 99999 + "/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + guestToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Booking not found"));
    }

    @Test
    void shouldGetBookingByIdForGuest() throws Exception {
        mockMvc.perform(get("/api/bookings/" + booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + guestToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.status").value(BookingStatus.PENDING.toString()));
    }

    @Test
    void shouldGetBookingByIdForHost() throws Exception {
        AuthenticationRequest requestForHost = new AuthenticationRequest("janedoe91@gmail.com", "password456");
        String hostToken = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestForHost)))
                .andReturn().getResponse().getContentAsString();
        hostToken = hostToken.substring(16, hostToken.length() - 2);

        mockMvc.perform(get("/api/bookings/" + booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + hostToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.status").value(BookingStatus.PENDING.toString()));
    }

    @Test
    void shouldFailGetBookingById() throws Exception {
        mockMvc.perform(get("/api/bookings/" + 99999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + guestToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Booking not found"));
    }

    @Test
    void shouldGetAllPaginatedBookingsForGuest() throws Exception {
        mockMvc.perform(get("/api/bookings/user")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + guestToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookings[0].id").value(booking.getId()))
                .andExpect(jsonPath("$.bookings[0].status").value(BookingStatus.PENDING.toString()));
    }

    @Test
    void shouldFailGetAllPaginatedBookingsForGuest() throws Exception {
        mockMvc.perform(get("/api/bookings/user")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnAllBookingsByPropertyId() throws Exception {
        AuthenticationRequest requestForHost = new AuthenticationRequest("janedoe91@gmail.com", "password456");
        String hostToken = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestForHost)))
                .andReturn().getResponse().getContentAsString();
        hostToken = hostToken.substring(16, hostToken.length() - 2);

        mockMvc.perform(get("/api/bookings/property/" + property.getId())
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + hostToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookings[0].id").value(booking.getId()))
                .andExpect(jsonPath("$.bookings[0].status").value(BookingStatus.PENDING.toString()));
    }

    @Test
    void shouldFailGetAllBookingsByPropertyId() throws Exception {
        AuthenticationRequest requestForHost = new AuthenticationRequest("janedoe91@gmail.com", "password456");
        String hostToken = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestForHost)))
                .andReturn().getResponse().getContentAsString();
        hostToken = hostToken.substring(16, hostToken.length() - 2);

        mockMvc.perform(get("/api/bookings/property/" + 99999)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + hostToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Property not found"));
    }

    @Test
    void shouldGetBookingDatesByPropertyId() throws Exception {
        mockMvc.perform(get("/api/bookings/property/" + property.getId() + "/dates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].checkInDate").isNotEmpty())
                .andExpect(jsonPath("$.[0].checkOutDate").isNotEmpty());
    }

    @Test
    void shouldFailGetBookingDatesByPropertyId() throws Exception {
        mockMvc.perform(get("/api/bookings/property/" + 99999 + "/dates"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Property not found"));
    }

    @Test
    void shouldSuccessfullyConfirmBookingById() throws Exception {
        AuthenticationRequest requestForHost = new AuthenticationRequest("janedoe91@gmail.com", "password456");
        String hostToken = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestForHost)))
                .andReturn().getResponse().getContentAsString();
        hostToken = hostToken.substring(16, hostToken.length() - 2);

        mockMvc.perform(put("/api/bookings/" + booking.getId() + "/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + hostToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(BookingStatus.CONFIRMED.toString()));
    }

    @Test
    void shouldFailToConfirmBookingById() throws Exception {
        AuthenticationRequest requestForHost = new AuthenticationRequest("janedoe91@gmail.com", "password456");
        String hostToken = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestForHost)))
                .andReturn().getResponse().getContentAsString();
        hostToken = hostToken.substring(16, hostToken.length() - 2);

        mockMvc.perform(put("/api/bookings/99999/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + hostToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Booking not found"));
    }
}
