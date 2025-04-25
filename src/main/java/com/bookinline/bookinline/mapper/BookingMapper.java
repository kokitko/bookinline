package com.bookinline.bookinline.mapper;

import com.bookinline.bookinline.dto.BookingRequestDto;
import com.bookinline.bookinline.dto.BookingResponseDto;
import com.bookinline.bookinline.dto.BookingResponsePage;
import com.bookinline.bookinline.entity.Booking;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.BookingStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public class BookingMapper {
    public static BookingResponseDto mapToBookingResponseDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .guestName(booking.getGuest().getFullName())
                .propertyTitle(booking.getProperty().getTitle())
                .status(String.valueOf(booking.getStatus()))
                .build();
    }

    public static Booking mapToBookingEntity(BookingRequestDto bookingRequestDto, Property property, User guest) {
        return Booking.builder()
                .checkInDate(bookingRequestDto.getCheckInDate())
                .checkOutDate(bookingRequestDto.getCheckOutDate())
                .property(property)
                .guest(guest)
                .status(BookingStatus.PENDING)
                .build();
    }

    public static BookingResponsePage mapToBookingResponsePage(Page<Booking> bookingPage,
                                                               List<BookingResponseDto> bookingResponseDtos) {
        BookingResponsePage bookingResponsePage = new BookingResponsePage();
        bookingResponsePage.setPage(bookingPage.getNumber());
        bookingResponsePage.setSize(bookingPage.getSize());
        bookingResponsePage.setTotalElements(bookingPage.getTotalElements());
        bookingResponsePage.setTotalPages(bookingPage.getTotalPages());
        bookingResponsePage.setLast(bookingPage.isLast());
        bookingResponsePage.setBookings(bookingResponseDtos);
        return bookingResponsePage;
    }
}
