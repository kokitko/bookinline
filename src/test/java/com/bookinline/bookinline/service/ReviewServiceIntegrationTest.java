package com.bookinline.bookinline.service;

import com.bookinline.bookinline.dto.ReviewRequestDto;
import com.bookinline.bookinline.dto.ReviewResponseDto;
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
import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ReviewServiceIntegrationTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private Flyway flyway;

    User guest = new User();
    User host = new User();
    Property property = new Property();
    Booking booking = new Booking();
    Review review = new Review();
    ReviewRequestDto reviewRequestDto = new ReviewRequestDto();

    @BeforeEach
    public void setup() {
        flyway.clean();
        flyway.migrate();
        guest.setId(1L);
        guest.setFullName("John Doe");
        guest.setEmail("johndoe88@gmail.com");
        guest.setPhoneNumber("1234567890");
        guest.setPassword("password123");
        guest.setStatus(UserStatus.ACTIVE);
        guest.setRole(Role.GUEST);
        guest = userRepository.save(guest);

        host.setId(2L);
        host.setFullName("Jane Doe");
        host.setEmail("janedoe91@gmail.com");
        host.setPhoneNumber("0987654321");
        host.setPassword("password456");
        host.setStatus(UserStatus.ACTIVE);
        host.setRole(Role.HOST);
        host = userRepository.save(host);

        property.setId(1L);
        property.setTitle("Cozy Apartment");
        property.setDescription("A cozy apartment in the city center.");
        property.setAddress("123 Main St, Cityville");
        property.setPricePerNight(new BigDecimal(100.0));
        property.setMaxGuests(4);
        property.setAvailable(true);
        property.setHost(host);
        property = propertyRepository.save(property);

        booking.setId(1L);
        booking.setProperty(property);
        booking.setGuest(guest);
        booking.setCheckInDate(LocalDate.of(2025, 4, 1));
        booking.setCheckOutDate(LocalDate.of(2025, 4, 7));
        booking.setStatus(BookingStatus.CHECKED_OUT);
        booking = bookingRepository.save(booking);

        review.setId(1L);
        review.setRating(5);
        review.setComment("Great stay!");
        review.setCreatedAt(LocalDateTime.of(2025, 4, 8, 10, 0));
        review.setAuthor(guest);
        review.setProperty(property);

        reviewRequestDto.setRating(5);
        reviewRequestDto.setComment("Great stay!");
    }

    @Test
    public void ReviewService_AddReview_ReturnsReviewResponseDto() {
        ReviewResponseDto reviewResponseDto = reviewService.addReview(property.getId(), guest.getId(), reviewRequestDto);

        Assertions.assertThat(reviewResponseDto).isNotNull();
        Assertions.assertThat(reviewResponseDto.getRating()).isEqualTo(reviewRequestDto.getRating());
        Assertions.assertThat(reviewResponseDto.getComment()).isEqualTo(reviewRequestDto.getComment());
        property = propertyRepository.findById(property.getId()).orElse(null);
        Assertions.assertThat(property.getAverageRating()).isEqualTo(5.0);
    }

    @Test
    public void ReviewService_DeleteReview_ReturnsVoid() {
        review = reviewRepository.save(review);
        reviewService.deleteReview(review.getId(), guest.getId());

        Review deletedReview = reviewRepository.findById(review.getId()).orElse(null);
        Assertions.assertThat(deletedReview).isNull();
        property = propertyRepository.findById(property.getId()).orElse(null);
        Assertions.assertThat(property.getAverageRating()).isEqualTo(0.0);
    }
}
