package com.bookinline.bookinline.integration.service;

import com.bookinline.bookinline.dto.BookingResponseDto;
import com.bookinline.bookinline.dto.PropertyResponseDto;
import com.bookinline.bookinline.dto.UserResponseDto;
import com.bookinline.bookinline.entity.Booking;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.Review;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.BookingStatus;
import com.bookinline.bookinline.entity.enums.PropertyType;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.entity.enums.UserStatus;
import com.bookinline.bookinline.repository.BookingRepository;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.repository.ReviewRepository;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.service.AdminService;
import com.bookinline.bookinline.service.S3Service;
import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AdminServiceIntegrationTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdminService adminService;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private Flyway flyway;
    @MockBean
    private S3Service s3Service;

    User admin = new User();
    User guest = new User();
    User host = new User();
    Property property = new Property();
    Booking booking = new Booking();
    Review review = new Review();

    @BeforeEach
    public void setup() {
        flyway.clean();
        flyway.migrate();

        admin.setFullName("Admin User");
        admin.setEmail("admin@admin.com");
        admin.setPassword("admin");
        admin.setPhoneNumber("6578767867");
        admin.setStatus(UserStatus.ACTIVE);
        admin.setRole(Role.ADMIN);
        admin = userRepository.save(admin);

        guest.setFullName("John Doe");
        guest.setEmail("johndoe88@gmail.com");
        guest.setPassword("password123");
        guest.setPhoneNumber("1234567890");
        guest.setStatus(UserStatus.ACTIVE);
        guest.setRole(Role.GUEST);
        guest = userRepository.save(guest);

        host.setFullName("Jane Doe");
        host.setEmail("janedoe91@gmail.com");
        host.setPassword("password456");
        host.setPhoneNumber("0987654321");
        host.setStatus(UserStatus.ACTIVE);
        host.setRole(Role.HOST);
        host = userRepository.save(host);

        property.setTitle("Beautiful Beach House");
        property.setDescription("A beautiful beach house with stunning views.");
        property.setCity("Miami, FL");
        property.setPropertyType(PropertyType.HOUSE);
        property.setFloorArea(150);
        property.setBedrooms(3);
        property.setAddress("123 Beach St");
        property.setPricePerNight(new BigDecimal(400.0));
        property.setAvailable(true);
        property.setMaxGuests(4);
        property.setHost(host);
        property = propertyRepository.save(property);

        booking.setCheckInDate(LocalDate.of(2026, 01, 01));
        booking.setCheckOutDate(LocalDate.of(2026, 01, 10));
        booking.setGuest(guest);
        booking.setProperty(property);
        booking.setStatus(BookingStatus.PENDING);
        booking = bookingRepository.save(booking);

        review.setRating(5);
        review.setComment("Amazing stay!");
        review.setAuthor(guest);
        review.setProperty(property);
        review.setCreatedAt(LocalDateTime.now());
        review = reviewRepository.save(review);
    }

    @Test
    public void AdminService_WarnUser_ReturnsUserResponseDto() {
        UserResponseDto userResponseDto = adminService.warnUser(
                guest.getId(), "Inappropriate behavior", admin.getId());

        User warnedUser = userRepository.findById(guest.getId()).orElseThrow();
        Assertions.assertThat(warnedUser).isNotNull();
        Assertions.assertThat(warnedUser.getStatus()).isEqualTo(UserStatus.WARNED);
        Assertions.assertThat(warnedUser.getStatusDescription()).isEqualTo("Inappropriate behavior");
    }

    @Test
    public void AdminService_BanUser_ReturnsUserResponseDto() {
        UserResponseDto userResponseDto = adminService.banUser(
                guest.getId(), "Inappropriate behavior", admin.getId());

        User bannedUser = userRepository.findById(guest.getId()).orElseThrow();
        Assertions.assertThat(bannedUser).isNotNull();
        Assertions.assertThat(bannedUser.getStatus()).isEqualTo(UserStatus.BANNED);
        Assertions.assertThat(bannedUser.getStatusDescription()).isEqualTo("Inappropriate behavior");
    }

    @Test
    public void AdminService_UnbanUser_ReturnsUserResponseDto() {
        UserResponseDto userResponseDto = adminService.unbanUser(
                guest.getId(), "Inappropriate behavior", admin.getId());

        User unbannedUser = userRepository.findById(guest.getId()).orElseThrow();
        Assertions.assertThat(unbannedUser).isNotNull();
        Assertions.assertThat(unbannedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
        Assertions.assertThat(unbannedUser.getStatusDescription()).isEqualTo("Inappropriate behavior");
    }

    @Test
    public void AdminService_ChangePropertyAvailability_ReturnsPropertyResponseDto() {
        PropertyResponseDto propertyResponseDto = adminService.changePropertyAvailability(
                property.getId(), admin.getId());

        Property updatedProperty = propertyRepository.findById(property.getId()).orElseThrow();
        Assertions.assertThat(updatedProperty).isNotNull();
        Assertions.assertThat(updatedProperty.getAvailable()).isFalse();
    }

    @Test
    public void AdminService_CancelBooking_ReturnsBookingResponseDto() {
        BookingResponseDto bookingResponseDto = adminService.cancelBooking(booking.getId(), admin.getId());

        Booking cancelledBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        Assertions.assertThat(cancelledBooking).isNotNull();
        Assertions.assertThat(cancelledBooking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    public void AdminService_DeleteReview_DeletesReview() {
        adminService.deleteReview(review.getId(), admin.getId());

        Review deletedReview = reviewRepository.findById(review.getId()).orElse(null);
        Assertions.assertThat(deletedReview).isNull();
    }
}
