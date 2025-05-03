package com.bookinline.bookinline.integration.repository;

import com.bookinline.bookinline.entity.Booking;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.BookingStatus;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.repository.BookingRepository;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Flyway flyway;

    User guest = new User();
    User host = new User();
    Property property = new Property();
    Booking booking1 = new Booking();
    Booking booking2 = new Booking();
    Booking booking3 = new Booking();
    Booking booking4 = new Booking();

    @BeforeEach
    public void setup() {
        flyway.clean();
        flyway.migrate();

        guest.setFullName("John Doe");
        guest.setEmail("johndoe88@gmail.com");
        guest.setPassword("password123");
        guest.setPhoneNumber("1234567890");
        guest.setRole(Role.GUEST);

        host.setFullName("Jane Doe");
        host.setEmail("janedoe91@gmail.com");
        host.setPassword("password456");
        host.setPhoneNumber("0987654321");
        host.setRole(Role.HOST);

        userRepository.saveAll(List.of(guest, host));

        property.setTitle("Luxury Villa");
        property.setDescription("A luxury villa with a sea view.");
        property.setAddress("456 Ocean Ave, Beach City");
        property.setPricePerNight(new BigDecimal("500.00"));
        property.setMaxGuests(6);
        property.setAvailable(true);
        property.setHost(host);
        propertyRepository.save(property);

        booking1.setCheckInDate(LocalDate.of(2025, 5, 1));
        booking1.setCheckOutDate(LocalDate.of(2025, 5, 7));
        booking1.setGuest(guest);
        booking1.setProperty(property);
        booking1.setStatus(BookingStatus.CONFIRMED);

        booking2.setCheckInDate(LocalDate.of(2025, 6, 1));
        booking2.setCheckOutDate(LocalDate.of(2025, 6, 7));
        booking2.setGuest(guest);
        booking2.setProperty(property);
        booking2.setStatus(BookingStatus.PENDING);

        booking3.setCheckInDate(LocalDate.of(2025, 4, 1));
        booking3.setCheckOutDate(LocalDate.of(2025, 4, 7));
        booking3.setGuest(guest);
        booking3.setProperty(property);
        booking3.setStatus(BookingStatus.CHECKED_OUT);

        booking4.setCheckInDate(LocalDate.of(2025, 5, 1));
        booking4.setCheckOutDate(LocalDate.of(2025, 5, 7));
        booking4.setGuest(guest);
        booking4.setProperty(property);
        booking4.setStatus(BookingStatus.CANCELLED);
    }

    @Test
    public void BookingRepository_FindById_ReturnsBooking() {
        bookingRepository.saveAll(List.of(booking1, booking2, booking3, booking4));
        Booking foundBooking = bookingRepository.findById(booking1.getId()).orElse(null);

        Assertions.assertThat(foundBooking).isNotNull();
        Assertions.assertThat(foundBooking.getId()).isEqualTo(booking1.getId());
    }

    @Test
    public void BookingRepository_FindByGuestId_ReturnsPage() {
        bookingRepository.saveAll(List.of(booking1, booking2, booking3, booking4));
        Pageable pageable = Pageable.ofSize(10);
        Page<Booking> bookings = bookingRepository.findByGuestId(guest.getId(), pageable);

        Assertions.assertThat(bookings).isNotNull();
        Assertions.assertThat(bookings.getTotalElements()).isEqualTo(4);
    }

    @Test
    public void BookingRepository_FindByPropertyId_ReturnsPage() {
        bookingRepository.saveAll(List.of(booking1, booking2, booking3, booking4));
        Pageable pageable = Pageable.ofSize(10);
        Page<Booking> bookings = bookingRepository.findByPropertyId(property.getId(), pageable);

        Assertions.assertThat(bookings).isNotNull();
        Assertions.assertThat(bookings.getTotalElements()).isEqualTo(4);
    }

    @Test
    public void BookingRepository_FindByPropertyIdAndGuestIdAndStatus_ReturnsBookings() {
        bookingRepository.saveAll(List.of(booking1, booking2, booking3, booking4));
        List<Booking> bookings = bookingRepository.findByPropertyIdAndGuestIdAndStatus(property.getId(),
                guest.getId(), BookingStatus.CONFIRMED);

        Assertions.assertThat(bookings).isNotNull();
        Assertions.assertThat(bookings.size()).isEqualTo(1);
        Assertions.assertThat(bookings.get(0).getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    public void BookingRepository_FindByStatusAndCheckOutDateBefore_ReturnsBookings() {
        bookingRepository.saveAll(List.of(booking1, booking2, booking3, booking4));
        List<Booking> bookings = bookingRepository.findByStatusAndCheckOutDateBefore(BookingStatus.CHECKED_OUT,
                LocalDate.of(2025, 5, 1));

        Assertions.assertThat(bookings).isNotNull();
        Assertions.assertThat(bookings.size()).isEqualTo(1);
        Assertions.assertThat(bookings.get(0).getStatus()).isEqualTo(BookingStatus.CHECKED_OUT);
    }

    @Test
    public void BookingRepository_FindByPropertyIdAndStatuses_ReturnsBookings() {
        bookingRepository.saveAll(List.of(booking1, booking2, booking3, booking4));
        List<Booking> bookings = bookingRepository.findByPropertyIdAndStatuses(property.getId(),
                List.of(BookingStatus.CANCELLED, BookingStatus.CONFIRMED));

        Assertions.assertThat(bookings).isNotNull();
        Assertions.assertThat(bookings.size()).isEqualTo(2);
    }

    @Test
    public void BookingRepository_ExistsByGuestIdAndHostIdAndStatuses_ReturnsTrue() {
        bookingRepository.saveAll(List.of(booking1, booking2, booking3, booking4));
        boolean exists = bookingRepository.existsByGuestIdAndHostIdAndStatuses(guest.getId(), host.getId(),
                List.of(BookingStatus.CANCELLED, BookingStatus.CONFIRMED));

        Assertions.assertThat(exists).isTrue();
    }
}
