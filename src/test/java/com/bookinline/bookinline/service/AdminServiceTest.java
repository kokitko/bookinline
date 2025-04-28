package com.bookinline.bookinline.service;

import com.bookinline.bookinline.dto.BookingResponseDto;
import com.bookinline.bookinline.dto.PropertyResponseDto;
import com.bookinline.bookinline.dto.ReviewResponseDto;
import com.bookinline.bookinline.dto.UserResponseDto;
import com.bookinline.bookinline.entity.Booking;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.Review;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.entity.enums.UserStatus;
import com.bookinline.bookinline.repository.BookingRepository;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.repository.ReviewRepository;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.service.impl.AdminServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @InjectMocks
    private AdminServiceImpl adminService;

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
    public void AdminService_GetUserById_ReturnsUserResponseDto() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(guest));

        UserResponseDto userResponseDto = adminService.getUserById(guest.getId(), 1L);
        Assertions.assertThat(userResponseDto).isNotNull();
        Assertions.assertThat(userResponseDto.getEmail()).isEqualTo(guest.getEmail());
    }

    @Test
    public void AdminService_GetPropertyById_ReturnsPropertyResponseDto() {
        when(propertyRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(property));

        PropertyResponseDto propertyResponseDto = adminService.getPropertyById(property.getId(), 1L);
        Assertions.assertThat(propertyResponseDto).isNotNull();
        Assertions.assertThat(propertyResponseDto.getId()).isEqualTo(property.getId());
    }

    @Test
    public void AdminService_GetBookingById_ReturnsBookingResponseDto() {
        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

        BookingResponseDto bookingResponseDto = adminService.getBookingById(booking.getId(), 1L);
        Assertions.assertThat(bookingResponseDto).isNotNull();
        Assertions.assertThat(bookingResponseDto.getId()).isEqualTo(booking.getId());
    }

    @Test
    public void AdminService_GetReviewById_ReturnsReviewResponseDto() {
        when(reviewRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(review));

        ReviewResponseDto responseDto = adminService.getReviewById(review.getId(), 1L);
        Assertions.assertThat(responseDto).isNotNull();
        Assertions.assertThat(responseDto.getId()).isEqualTo(review.getId());
    }
}
