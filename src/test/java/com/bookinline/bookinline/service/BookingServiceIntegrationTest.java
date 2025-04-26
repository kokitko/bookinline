package com.bookinline.bookinline.service;

import com.bookinline.bookinline.dto.BookingRequestDto;
import com.bookinline.bookinline.dto.BookingResponseDto;
import com.bookinline.bookinline.entity.Booking;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.BookingStatus;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.entity.enums.UserStatus;
import com.bookinline.bookinline.repository.BookingRepository;
import com.bookinline.bookinline.repository.PropertyRepository;
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

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BookingServiceIntegrationTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private Flyway flyway;


    User guest = new User();
    User host = new User();
    Property property = new Property();
    Booking booking1 = new Booking();
    Booking booking2 = new Booking();
    BookingRequestDto bookingRequestDto = new BookingRequestDto();

    @BeforeEach
    public void setup() {
        flyway.clean();
        flyway.migrate();
        guest.setFullName("John Doe");
        guest.setEmail("johndoe88@gmail.com");
        guest.setPhoneNumber("1234567890");
        guest.setPassword("password123");
        guest.setStatus(UserStatus.ACTIVE);
        guest.setRole(Role.GUEST);
        guest = userRepository.save(guest);

        host.setFullName("Jane Doe");
        host.setEmail("janedoe91@gmail.com");
        host.setPhoneNumber("0987654321");
        host.setPassword("password456");
        host.setStatus(UserStatus.ACTIVE);
        host.setRole(Role.HOST);
        host = userRepository.save(host);

        property.setTitle("Luxury Villa");
        property.setDescription("A luxury villa with a sea view.");
        property.setAddress("456 Ocean Ave, Beach City");
        property.setPricePerNight(new BigDecimal("500.00"));
        property.setMaxGuests(6);
        property.setHost(host);
        property = propertyRepository.save(property);

        booking1.setProperty(property);
        booking1.setGuest(guest);
        booking1.setCheckInDate(LocalDate.of(2025, 5, 1));
        booking1.setCheckOutDate(LocalDate.of(2025, 5, 7));
        booking1.setStatus(BookingStatus.CONFIRMED);
        booking1 = bookingRepository.save(booking1);

        booking2.setProperty(property);
        booking2.setGuest(guest);
        booking2.setCheckInDate(LocalDate.of(2025, 6, 1));
        booking2.setCheckOutDate(LocalDate.of(2025, 6, 7));
        booking2.setStatus(BookingStatus.PENDING);
        booking2 = bookingRepository.save(booking2);

        bookingRequestDto.setCheckInDate(LocalDate.of(2025, 10, 1));
        bookingRequestDto.setCheckOutDate(LocalDate.of(2025, 10, 7));
    }

    @Test
    public void BookingService_BookProperty_ReturnsBookingResponseDto() {
        BookingResponseDto responseDto = bookingService.bookProperty(bookingRequestDto, property.getId(), 1L);

        Booking booking = bookingRepository.findById(responseDto.getId()).orElse(null);

        Assertions.assertThat(booking).isNotNull();
        Assertions.assertThat(booking.getCheckInDate()).isEqualTo(bookingRequestDto.getCheckInDate());
        Assertions.assertThat(booking.getCheckOutDate()).isEqualTo(bookingRequestDto.getCheckOutDate());
        Assertions.assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
    }

    @Test
    public void BookingService_CancelBooking_ReturnsBookingResponseDto() {
        Booking booking = bookingRepository.findById(booking1.getId()).orElse(null);
        Assertions.assertThat(booking).isNotNull();
        Assertions.assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);

        bookingService.cancelBooking(booking1.getId(), guest.getId());

        Booking cancelledBooking = bookingRepository.findById(booking1.getId()).orElse(null);
        Assertions.assertThat(cancelledBooking).isNotNull();
        Assertions.assertThat(cancelledBooking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    public void BookingService_ConfirmBooking_ReturnsBookingResponseDto() {
        Booking booking = bookingRepository.findById(booking2.getId()).orElse(null);
        Assertions.assertThat(booking).isNotNull();
        Assertions.assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);

        bookingService.confirmBooking(booking2.getId(), host.getId());

        Booking confirmedBooking = bookingRepository.findById(booking2.getId()).orElse(null);
        Assertions.assertThat(confirmedBooking).isNotNull();
        Assertions.assertThat(confirmedBooking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }
}
