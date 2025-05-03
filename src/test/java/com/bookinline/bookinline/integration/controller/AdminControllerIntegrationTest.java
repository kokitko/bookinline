package com.bookinline.bookinline.integration.controller;

import com.bookinline.bookinline.dto.AuthenticationRequest;
import com.bookinline.bookinline.entity.Booking;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.Review;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.BookingStatus;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.entity.enums.UserStatus;
import com.bookinline.bookinline.repository.BookingRepository;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.repository.ReviewRepository;
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
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AdminControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private Flyway flyway;

    User guest = new User();
    User host = new User();
    User admin = new User();
    Property property = new Property();
    Booking booking = new Booking();
    Review review = new Review();
    String adminToken;

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

        admin.setFullName("admin");
        admin.setEmail("admin@admin.com");
        admin.setPassword(passwordEncoder.encode("adminadmin"));
        admin.setPhoneNumber("1234567890");
        admin.setRole(Role.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);
        admin = userRepository.save(admin);

        property.setTitle("Beautiful Beach House");
        property.setDescription("A beautiful beach house with stunning views.");
        property.setAddress("123 Beach Ave, Miami, FL");
        property.setPricePerNight(new BigDecimal(250.0));
        property.setHost(host);
        property.setMaxGuests(6);
        property.setAvailable(true);
        property = propertyRepository.save(property);

        booking.setProperty(property);
        booking.setGuest(guest);
        booking.setCheckInDate(LocalDate.now().minusDays(3));
        booking.setCheckOutDate(LocalDate.now());
        booking.setStatus(BookingStatus.CHECKED_OUT);
        booking = bookingRepository.save(booking);

        review.setRating(5);
        review.setComment("Amazing stay! Highly recommend.");
        review.setProperty(property);
        review.setAuthor(guest);
        review.setCreatedAt(LocalDateTime.now());
        review = reviewRepository.save(review);

        AuthenticationRequest authenticationRequest = new AuthenticationRequest("admin@admin.com", "adminadmin");
        adminToken = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andReturn().getResponse().getContentAsString();
        adminToken = adminToken.substring(10, adminToken.length() - 2);
    }

    @Test
    void shouldReturnUserDetailsById() throws Exception {
        mockMvc.perform(get("/api/admin/users/" + guest.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value(guest.getFullName()));
    }

    @Test
    void shouldFailToReturnUserDetailsToNotAdmin() throws Exception {
        AuthenticationRequest guestRequest = new AuthenticationRequest("johndoe88@gmail.com", "password123");
        String guestToken = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(guestRequest)))
                .andReturn().getResponse().getContentAsString();
        guestToken = guestToken.substring(10, guestToken.length() - 2);

        mockMvc.perform(get("/api/admin/users/" + guest.getId())
                        .header("Authorization", "Bearer " + guestToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFailToReturnUserDetailsById() throws Exception {
        mockMvc.perform(get("/api/admin/users/99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldSuccessfullyReturnPropertyById() throws Exception {
        mockMvc.perform(get("/api/admin/properties/" + property.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(property.getTitle()));
    }

    @Test
    void shouldFailToReturnPropertyById() throws Exception {
        mockMvc.perform(get("/api/admin/properties/99999")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSuccessfullyReturnBookingById() throws Exception {
        mockMvc.perform(get("/api/admin/bookings/" + booking.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()));
    }

    @Test
    void shouldFailToReturnBookingById() throws Exception {
        mockMvc.perform(get("/api/admin/bookings/99999")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSuccessfullyReturnReviewById() throws Exception {
        mockMvc.perform(get("/api/admin/reviews/" + review.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(review.getId()));
    }

    @Test
    void shouldFailToReturnReviewById() throws Exception {
        mockMvc.perform(get("/api/admin/reviews/99999")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSuccessfullyWarnUser() throws Exception {
        String reason = "Inappropriate behavior";
        mockMvc.perform(put("/api/admin/warn/" + guest.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .param("reason", "Inappropriate behavior"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(UserStatus.WARNED.toString()));
    }

    @Test
    void shouldFailToWarnUser() throws Exception {
        mockMvc.perform(put("/api/admin/warn/99999")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("reason", "Inappropriate behavior"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSuccessfullyBanUser() throws Exception {
        String reasonJson = "{\"reason\": \"Inappropriate behavior\"}";
        mockMvc.perform(delete("/api/admin/ban/" + guest.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .param("reason", "Inappropriate behavior"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(UserStatus.BANNED.toString()));
    }

    @Test
    void shouldFailToBanUser() throws Exception {
        String reasonJson = "{\"reason\": \"Inappropriate behavior\"}";
        mockMvc.perform(delete("/api/admin/ban/99999")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("reason", "Inappropriate behavior"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSuccessfullyUnbanUser() throws Exception {
        mockMvc.perform(put("/api/admin/unban/" + guest.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .param("reason", "Inappropriate behavior"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(UserStatus.ACTIVE.toString()));
    }

    @Test
    void shouldFailToUnbanUser() throws Exception {
        mockMvc.perform(put("/api/admin/unban/99999")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("reason", "Inappropriate behavior"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSuccessfullyChangePropertyAvailability() throws Exception {
        mockMvc.perform(put("/api/admin/property/" + property.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void shouldFailToChangePropertyAvailability() throws Exception {
        mockMvc.perform(put("/api/admin/property/99999")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSuccessfullyCancelBooking() throws Exception {
        mockMvc.perform(delete("/api/admin/booking/" + booking.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(BookingStatus.CANCELLED.toString()));
    }

    @Test
    void shouldFailToCancelBooking() throws Exception {
        mockMvc.perform(delete("/api/admin/booking/99999")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSuccessfullyDeleteReview() throws Exception {
        mockMvc.perform(delete("/api/admin/review/" + review.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldFailToDeleteReview() throws Exception {
        mockMvc.perform(delete("/api/admin/review/99999")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
