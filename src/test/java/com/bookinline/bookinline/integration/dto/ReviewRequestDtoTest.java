package com.bookinline.bookinline.integration.dto;

import com.bookinline.bookinline.dto.AuthenticationRequest;
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
import com.bookinline.bookinline.service.S3Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ReviewRequestDtoTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Flyway flyway;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @MockBean
    private S3Service s3Service;

    User guest;
    User host;
    Property property;
    Booking booking;
    String token;

    @BeforeEach
    public void setup() throws Exception {
        flyway.clean();
        flyway.migrate();
        userRepository.deleteAll();

        guest = new User();
        guest.setFullName("John Doe");
        guest.setEmail("johndoe88@gmail.com");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        guest.setPassword(passwordEncoder.encode("password123"));
        guest.setRole(Role.GUEST);
        guest.setStatus(UserStatus.ACTIVE);
        guest.setPhoneNumber("1234567890");
        guest = userRepository.save(guest);

        host = new User();
        host.setFullName("Jane Doe");
        host.setEmail("janedoe91@gmail.com");
        host.setPassword(passwordEncoder.encode("password123"));
        host.setRole(Role.HOST);
        host.setStatus(UserStatus.ACTIVE);
        host.setPhoneNumber("0987654321");
        host = userRepository.save(host);

        property = new Property();
        property.setTitle("Cozy Cottage");
        property.setDescription("A cozy cottage in the countryside.");
        property.setAddress("123 Country Lane");
        property.setPricePerNight(new BigDecimal(100.00));
        property.setCity("Countryside");
        property.setPropertyType(PropertyType.COTTAGE);
        property.setMaxGuests(5);
        property.setFloorArea(80);
        property.setBedrooms(3);
        property.setHost(host);
        property = propertyRepository.save(property);

        booking = new Booking();
        booking.setProperty(property);
        booking.setGuest(guest);
        booking.setCheckInDate(LocalDate.of(2023, 10, 1));
        booking.setCheckOutDate(LocalDate.of(2023, 10, 8));
        booking.setStatus(BookingStatus.CHECKED_OUT);
        booking = bookingRepository.save(booking);

        AuthenticationRequest request = new AuthenticationRequest("johndoe88@gmail.com", "password123");
        token = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
        token = token.substring(16, token.length() - 2);
    }

    @Test
    public void testRatingLessThanOne() throws Exception {
        String request = """
                    {
                        "rating": 0,
                        "comment": "Great stay!"
                    }
                """;

        mockMvc.perform(post("/api/reviews/property/1/review")
                        .content(request)
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Validation failed: rating: Rating must be between 1 and 5"));
    }

    @Test
    public void testRatingGreaterThanFive() throws Exception {
        String request = """
                    {
                        "rating": 6,
                        "comment": "Great stay!"
                    }
                """;

        mockMvc.perform(post("/api/reviews/property/1/review")
                        .content(request)
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Validation failed: rating: Rating must be between 1 and 5"));
    }

    @Test
    public void testCommentIsBlank() throws Exception {
        String request = """
                    {
                        "rating": 4,
                        "comment": ""
                    }
                """;

        mockMvc.perform(post("/api/reviews/property/1/review")
                        .content(request)
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Validation failed: comment: Comment cannot be blank"));
    }
}
