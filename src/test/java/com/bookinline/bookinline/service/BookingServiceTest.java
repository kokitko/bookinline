package com.bookinline.bookinline.service;

import com.bookinline.bookinline.dto.BookingDatesDto;
import com.bookinline.bookinline.dto.BookingResponsePage;
import com.bookinline.bookinline.entity.Booking;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.BookingStatus;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.entity.enums.UserStatus;
import com.bookinline.bookinline.repository.BookingRepository;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.service.impl.BookingServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    User guest = new User();
    User host = new User();
    Property property = new Property();
    Booking booking1 = new Booking();
    Booking booking2 = new Booking();

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
        property.setTitle("Luxury Villa");
        property.setDescription("A luxury villa with a sea view.");
        property.setAddress("456 Ocean Ave, Beach City");
        property.setPricePerNight(new BigDecimal("500.00"));
        property.setMaxGuests(6);
        property.setHost(host);

        booking1.setId(1L);
        booking1.setProperty(property);
        booking1.setGuest(guest);
        booking1.setCheckInDate(LocalDate.of(2025, 5, 1));
        booking1.setCheckOutDate(LocalDate.of(2025, 5, 7));
        booking1.setStatus(BookingStatus.CONFIRMED);

        booking2.setId(2L);
        booking2.setProperty(property);
        booking2.setGuest(guest);
        booking2.setCheckInDate(LocalDate.of(2025, 6, 1));
        booking2.setCheckOutDate(LocalDate.of(2025, 6, 7));
        booking2.setStatus(BookingStatus.PENDING);
    }

    @Test
    public void BookingService_GetBookingsByUserId_ReturnsBookingResponsePage() {
        Page<Booking> mockPage = Mockito.mock(Page.class);
        when(bookingRepository.findByGuestId(Mockito.anyLong(), Mockito.any(Pageable.class))).thenReturn(mockPage);
        when(mockPage.getNumber()).thenReturn(0);
        when(mockPage.getSize()).thenReturn(10);
        when(mockPage.getTotalElements()).thenReturn(2L);
        when(mockPage.getTotalPages()).thenReturn(1);
        when(mockPage.isLast()).thenReturn(true);
        when(mockPage.getContent()).thenReturn(List.of(booking1, booking2));

        BookingResponsePage responsePage = bookingService.getBookingsByUserId(guest.getId(), 0, 10);

        Assertions.assertThat(responsePage).isNotNull();
        Assertions.assertThat(responsePage.getTotalElements()).isEqualTo(2L);
        Assertions.assertThat(responsePage.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(responsePage.getBookings()).hasSize(2);
        Assertions.assertThat(responsePage.getBookings().getFirst().getPropertyTitle())
                .isEqualTo(booking1.getProperty().getTitle());
    }

    @Test
    public void BookingService_GetBookingsByPropertyId_ReturnsBookingResponsePage() {
        Page<Booking> mockPage = Mockito.mock(Page.class);
        when(bookingRepository.findByPropertyId(Mockito.anyLong(), Mockito.any(Pageable.class))).thenReturn(mockPage);
        when(propertyRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(property));
        when(mockPage.getNumber()).thenReturn(0);
        when(mockPage.getSize()).thenReturn(10);
        when(mockPage.getTotalElements()).thenReturn(2L);
        when(mockPage.getTotalPages()).thenReturn(1);
        when(mockPage.isLast()).thenReturn(true);
        when(mockPage.getContent()).thenReturn(List.of(booking1, booking2));

        BookingResponsePage responsePage = bookingService.getBookingsByPropertyId(
                property.getId(), host.getId(), 0, 10);

        Assertions.assertThat(responsePage).isNotNull();
        Assertions.assertThat(responsePage.getTotalElements()).isEqualTo(2L);
        Assertions.assertThat(responsePage.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(responsePage.getBookings()).hasSize(2);
        Assertions.assertThat(responsePage.getBookings().getFirst().getPropertyTitle())
                .isEqualTo(booking1.getProperty().getTitle());
    }

    @Test
    public void BookingService_GetBookedDatesByPropertyId_ReturnsBookedDatesDto() {
        when(bookingRepository.findByPropertyIdAndStatuses(Mockito.anyLong(), Mockito.anyList()))
                .thenReturn(List.of(booking1, booking2));

        List<BookingDatesDto> bookings = bookingService.getBookedDatesByPropertyId(property.getId());

        Assertions.assertThat(bookings).isNotNull();
        Assertions.assertThat(bookings).hasSize(2);
        Assertions.assertThat(bookings.getFirst().getStartDate()).isEqualTo(booking1.getCheckInDate());
    }
}
