package com.bookinline.bookinline.service;

import com.bookinline.bookinline.dto.BookingResponsePage;
import com.bookinline.bookinline.dto.ReviewResponsePage;
import com.bookinline.bookinline.entity.Booking;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.Review;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.entity.enums.UserStatus;
import com.bookinline.bookinline.repository.BookingRepository;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.repository.ReviewRepository;
import com.bookinline.bookinline.service.impl.ReviewServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ReviewServiceTest {
    @Mock
    private ReviewRepository reviewRepository;
    @InjectMocks
    private ReviewServiceImpl reviewService;

    User guest = new User();
    User host = new User();
    Property property = new Property();
    Booking booking = new Booking();
    Review review = new Review();

    @BeforeEach
    public void setup() {
        guest.setId(1L);
        guest.setFullName("John Doe");
        guest.setEmail("johndoe88@gmail.com");
        guest.setPhoneNumber("1234567890");
        guest.setPassword("password123");
        guest.setStatus(UserStatus.ACTIVE);
        guest.setRole(Role.GUEST);

        host.setId(2L);
        host.setFullName("Jane Doe");
        host.setEmail("janedoe91@gmail.com");
        host.setPhoneNumber("0987654321");
        host.setPassword("password456");
        host.setStatus(UserStatus.ACTIVE);
        host.setRole(Role.HOST);

        property.setId(1L);
        property.setTitle("Cozy Apartment");
        property.setDescription("A cozy apartment in the city center.");
        property.setAddress("123 Main St, Cityville");
        property.setPricePerNight(new BigDecimal(100.0));
        property.setAvailable(true);
        property.setHost(host);

        booking.setId(1L);
        booking.setProperty(property);
        booking.setGuest(guest);
        booking.setCheckInDate(LocalDate.of(2025, 4, 1));
        booking.setCheckOutDate(LocalDate.of(2025, 4, 7));

        review.setId(1L);
        review.setRating(5);
        review.setComment("Great stay!");
        review.setCreatedAt(LocalDateTime.of(2025, 4, 8, 10, 0));
        review.setAuthor(guest);
        review.setProperty(property);
    }

    @Test
    public void ReviewService_GetReviewsByPropertyId_ReturnsReviewResponsePage() {
        Page<Review> mockPage = Mockito.mock(Page.class);
        when(reviewRepository.findByPropertyId(Mockito.anyLong(), Mockito.any(Pageable.class))).thenReturn(mockPage);
        when(mockPage.getNumber()).thenReturn(0);
        when(mockPage.getSize()).thenReturn(10);
        when(mockPage.getTotalElements()).thenReturn(1L);
        when(mockPage.getTotalPages()).thenReturn(1);
        when(mockPage.isLast()).thenReturn(true);
        when(mockPage.getContent()).thenReturn(List.of(review));

        ReviewResponsePage responsePage = reviewService.getReviewsByPropertyId(property.getId(), 0, 10);

        Assertions.assertThat(responsePage).isNotNull();
        Assertions.assertThat(responsePage.getTotalElements()).isEqualTo(1L);
        Assertions.assertThat(responsePage.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(responsePage.getReviews()).hasSize(1);
        Assertions.assertThat(responsePage.getReviews().getFirst().getComment())
                .isEqualTo(review.getComment());
    }

    @Test
    public void ReviewService_GetReviewsByUserId_ReturnsReviewResponsePage() {
        Page<Review> mockPage = Mockito.mock(Page.class);
        when(reviewRepository.findByAuthorId(Mockito.anyLong(), Mockito.any(Pageable.class))).thenReturn(mockPage);
        when(mockPage.getNumber()).thenReturn(0);
        when(mockPage.getSize()).thenReturn(10);
        when(mockPage.getTotalElements()).thenReturn(1L);
        when(mockPage.getTotalPages()).thenReturn(1);
        when(mockPage.isLast()).thenReturn(true);
        when(mockPage.getContent()).thenReturn(List.of(review));

        ReviewResponsePage responsePage = reviewService.getReviewsByUserId(guest.getId(), 0, 10);

        Assertions.assertThat(responsePage).isNotNull();
        Assertions.assertThat(responsePage.getTotalElements()).isEqualTo(1L);
        Assertions.assertThat(responsePage.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(responsePage.getReviews()).hasSize(1);
        Assertions.assertThat(responsePage.getReviews().getFirst().getComment())
                .isEqualTo(review.getComment());
    }
}
