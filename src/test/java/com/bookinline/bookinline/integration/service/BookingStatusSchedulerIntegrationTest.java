package com.bookinline.bookinline.integration.service;

import com.bookinline.bookinline.entity.Booking;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.BookingStatus;
import com.bookinline.bookinline.entity.enums.PropertyType;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.repository.BookingRepository;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.service.impl.BookingStatusScheduler;
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
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BookingStatusSchedulerIntegrationTest {
    @Autowired
    private BookingStatusScheduler bookingStatusScheduler;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private Flyway flyway;

    User guest = new User();
    User host = new User();
    Property property = new Property();
    Booking booking1 = new Booking();
    Booking booking2 = new Booking();
    Booking booking3 = new Booking();

    @BeforeEach
    public void setup() {

        flyway.clean();
        flyway.migrate();

        guest.setFullName("John Doe");
        guest.setEmail("johndoe88@gmail.com");
        guest.setPassword("password123");
        guest.setPhoneNumber("1234567890");
        guest.setRole(Role.GUEST);
        guest = userRepository.save(guest);

        host.setFullName("Jane Doe");
        host.setEmail("janedoe91@gmail.com");
        host.setPassword("password456");
        host.setPhoneNumber("0987654321");
        host.setRole(Role.HOST);
        host = userRepository.save(host);

        property.setTitle("Luxury Villa");
        property.setDescription("A luxury villa with a sea view.");
        property.setCity("Beach City");
        property.setFloorArea(200);
        property.setBedrooms(3);
        property.setPropertyType(PropertyType.VILLA);
        property.setAddress("456 Ocean Ave");
        property.setPricePerNight(new BigDecimal("500.00"));
        property.setMaxGuests(6);
        property.setAvailable(true);
        property.setHost(host);
        propertyRepository.save(property);

        booking1.setCheckInDate(LocalDate.of(2025, 04, 01));
        booking1.setCheckOutDate(LocalDate.of(2025, 04, 05));
        booking1.setStatus(BookingStatus.CONFIRMED);
        booking1.setProperty(property);
        booking1.setGuest(guest);
        booking1 = bookingRepository.save(booking1);

        booking2.setCheckInDate(LocalDate.of(2025, 04, 06));
        booking2.setCheckOutDate(LocalDate.of(2025, 04, 25));
        booking2.setStatus(BookingStatus.CONFIRMED);
        booking2.setProperty(property);
        booking2.setGuest(guest);
        booking2 = bookingRepository.save(booking2);

        booking3.setCheckInDate(LocalDate.of(2028, 01, 01));
        booking3.setCheckOutDate(LocalDate.of(2028, 01, 05));
        booking3.setStatus(BookingStatus.CONFIRMED);
        booking3.setProperty(property);
        booking3.setGuest(guest);
        booking3 = bookingRepository.save(booking3);
    }

    @Test
    public void BookingStatusScheduler_UpdateBookingStatusToCheckedOut_ReturnsVoid() {
        bookingStatusScheduler.updateBookingStatusToCheckedOut();

        booking1 = bookingRepository.findById(booking1.getId()).orElse(null);
        booking2 = bookingRepository.findById(booking2.getId()).orElse(null);
        booking3 = bookingRepository.findById(booking3.getId()).orElse(null);
        Assertions.assertThat(booking1).isNotNull();
        Assertions.assertThat(booking1.getStatus()).isEqualTo(BookingStatus.CHECKED_OUT);
        Assertions.assertThat(booking2).isNotNull();
        Assertions.assertThat(booking2.getStatus()).isEqualTo(BookingStatus.CHECKED_OUT);
        Assertions.assertThat(booking3).isNotNull();
        Assertions.assertThat(booking3.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }
}
