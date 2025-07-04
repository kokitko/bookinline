package com.bookinline.bookinline.unit.mapper;

import com.bookinline.bookinline.dto.BookingRequestDto;
import com.bookinline.bookinline.dto.BookingResponseDto;
import com.bookinline.bookinline.dto.BookingResponsePage;
import com.bookinline.bookinline.entity.Booking;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.BookingStatus;
import com.bookinline.bookinline.mapper.BookingMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingMapperTest {
    private final BookingMapper bookingMapper = new BookingMapper();

    @Test
    void shouldMapToBookingResponseDto() {
        User user = new User();
        user.setFullName("John Doe");
        User host = new User();
        host.setId(1L);
        host.setFullName("Jane Doe");
        Property property = new Property();
        property.setTitle("Test Property");
        property.setHost(host);
        Booking booking = new Booking(1L, LocalDate.now(), LocalDate.now().plusDays(2),
                user, property, BookingStatus.PENDING);

        BookingResponseDto responseDto = bookingMapper.mapToBookingResponseDto(booking);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getId()).isEqualTo(booking.getId());
        assertThat(responseDto.getCheckInDate()).isEqualTo(booking.getCheckInDate());
        assertThat(responseDto.getCheckOutDate()).isEqualTo(booking.getCheckOutDate());
        assertThat(responseDto.getGuestName()).isEqualTo(booking.getGuest().getFullName());
        assertThat(responseDto.getPropertyTitle()).isEqualTo(booking.getProperty().getTitle());
        assertThat(responseDto.getStatus()).isEqualTo(booking.getStatus().name());
        assertThat(responseDto.getPropertyId()).isEqualTo(booking.getProperty().getId());
        assertThat(responseDto.getHostId()).isEqualTo(booking.getProperty().getHost().getId());
    }

    @Test
    void shouldMapToBookingEntity() {
        BookingRequestDto requestDto = new BookingRequestDto(LocalDate.now(), LocalDate.now().plusDays(2));
        User user = new User();
        user.setFullName("John Doe");
        Property property = new Property();
        property.setTitle("Test Property");

        Booking booking = BookingMapper.mapToBookingEntity(requestDto, property, user);

        assertThat(booking).isNotNull();
        assertThat(booking.getCheckInDate()).isEqualTo(requestDto.getCheckInDate());
        assertThat(booking.getCheckOutDate()).isEqualTo(requestDto.getCheckOutDate());
        assertThat(booking.getGuest()).isEqualTo(user);
        assertThat(booking.getProperty()).isEqualTo(property);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
    }

    @Test
    void shouldMapToBookingResponsePage() {
        User host = new User();
        host.setId(1L);
        host.setFullName("Jane Doe");
        Property property = new Property();
        property.setTitle("Test Property");
        property.setHost(host);
        List<Booking> bookings = List.of(
                new Booking(1L, LocalDate.now(), LocalDate.now().plusDays(2), mock(User.class), property, BookingStatus.PENDING),
                new Booking(2L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), mock(User.class), property, BookingStatus.CONFIRMED)
        );

        Page<Booking> bookingPage = mock(Page.class);
        when(bookingPage.getNumber()).thenReturn(0);
        when(bookingPage.getSize()).thenReturn(2);
        when(bookingPage.getTotalElements()).thenReturn(2L);
        when(bookingPage.getTotalPages()).thenReturn(1);
        when(bookingPage.isLast()).thenReturn(true);
        when(bookingPage.getContent()).thenReturn(bookings);

        BookingResponsePage bookingResponsePage = BookingMapper.mapToBookingResponsePage(bookingPage);

        assertThat(bookingResponsePage).isNotNull();
        assertThat(bookingResponsePage.getPage()).isEqualTo(0);
        assertThat(bookingResponsePage.getSize()).isEqualTo(2);
        assertThat(bookingResponsePage.getTotalElements()).isEqualTo(2L);
        assertThat(bookingResponsePage.getTotalPages()).isEqualTo(1);
        assertThat(bookingResponsePage.isLast()).isTrue();
        assertThat(bookingResponsePage.getBookings()).hasSize(2);
        assertThat(bookingResponsePage.getBookings().get(0).getId()).isEqualTo(bookings.get(0).getId());
        assertThat(bookingResponsePage.getBookings().get(1).getId()).isEqualTo(bookings.get(1).getId());
    }
}
