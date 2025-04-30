package com.bookinline.bookinline.controller;

import com.bookinline.bookinline.dto.AuthenticationRequest;
import com.bookinline.bookinline.dto.ReviewRequestDto;
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
public class ReviewControllerIntegrationTest {
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

    User host = new User();
    User guest = new User();
    Property property = new Property();
    Booking booking = new Booking();
    Review review = new Review();
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

        AuthenticationRequest authenticationRequest = new AuthenticationRequest("johndoe88@gmail.com", "password123");
        guestToken = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andReturn().getResponse().getContentAsString();
        guestToken = guestToken.substring(10, guestToken.length() - 2);
    }

    @Test
    void shouldSuccessfullyCreateReview() throws Exception {
        reviewRepository.deleteAll();
        ReviewRequestDto reviewRequestDto = new ReviewRequestDto(5,"Great stay!");

        mockMvc.perform(post("/api/reviews/property/" + property.getId())
                        .header("Authorization", "Bearer " + guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(reviewRequestDto.getRating()))
                .andExpect(jsonPath("$.comment").value(reviewRequestDto.getComment()));
    }

    @Test
    void shouldFailToCreateReviewWhenPropertyNotFound() throws Exception {
        ReviewRequestDto reviewRequestDto = new ReviewRequestDto(5,"Great stay!");

        mockMvc.perform(post("/api/reviews/property/99999")
                        .header("Authorization", "Bearer " + guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSuccessfullyReturnReviewById() throws Exception {
        mockMvc.perform(get("/api/reviews/" + review.getId())
                        .header("Authorization", "Bearer " + guestToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(review.getId()))
                .andExpect(jsonPath("$.rating").value(review.getRating()))
                .andExpect(jsonPath("$.comment").value(review.getComment()));
    }

    @Test
    void shouldFailToReturnReviewWhenNotFound() throws Exception {
        mockMvc.perform(get("/api/reviews/99999")
                        .header("Authorization", "Bearer " + guestToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSuccessfullyDeleteReview() throws Exception {
        mockMvc.perform(delete("/api/reviews/" + review.getId())
                        .header("Authorization", "Bearer " + guestToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldFailToDeleteReviewWhenNotFound() throws Exception {
        mockMvc.perform(delete("/api/reviews/99999")
                        .header("Authorization", "Bearer " + guestToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSuccessfullyGetReviewsByPropertyId() throws Exception {
        mockMvc.perform(get("/api/reviews/property/" + property.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviews[0].id").value(review.getId()))
                .andExpect(jsonPath("$.reviews[0].rating").value(review.getRating()))
                .andExpect(jsonPath("$.reviews[0].comment").value(review.getComment()));
    }

    @Test
    void shouldFailToGetReviewsWhenPropertyNotFound() throws Exception {
        mockMvc.perform(get("/api/reviews/property/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSuccessfullyReturnPaginatedReviewsByUserId() throws Exception {
        mockMvc.perform(get("/api/reviews/user/" + guest.getId())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviews[0].id").value(review.getId()))
                .andExpect(jsonPath("$.reviews[0].rating").value(review.getRating()))
                .andExpect(jsonPath("$.reviews[0].comment").value(review.getComment()));
    }

    @Test
    void shouldFailToReturnPaginatedReviewsWhenUserNotFound() throws Exception {
        mockMvc.perform(get("/api/reviews/user/99999")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isNotFound());
    }
}
