package com.bookinline.bookinline.service;

import com.bookinline.bookinline.dto.BookingRequestDto;
import com.bookinline.bookinline.dto.BookingResponseDto;
import com.bookinline.bookinline.dto.BookingResponsePage;

import java.time.LocalDate;

public interface BookingService {
    BookingResponseDto bookProperty(BookingRequestDto bookingRequestDto, Long propertyId, Long userId);
    BookingResponseDto cancelBooking(Long bookingId);
    BookingResponsePage getBookingsByUserId(Long userId, int page, int size);
    BookingResponsePage getBookingsByPropertyId(Long propertyId, int page, int size);
    BookingResponseDto confirmBooking(Long bookingId);
    boolean isPropertyAvailable(Long propertyId, LocalDate startDate, LocalDate endDate);
}
