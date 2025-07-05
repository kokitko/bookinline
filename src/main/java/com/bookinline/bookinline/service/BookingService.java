package com.bookinline.bookinline.service;

import com.bookinline.bookinline.dto.BookingDatesDto;
import com.bookinline.bookinline.dto.BookingRequestDto;
import com.bookinline.bookinline.dto.BookingResponseDto;
import com.bookinline.bookinline.dto.BookingResponsePage;

import java.util.List;

public interface BookingService {
    BookingResponseDto bookProperty(BookingRequestDto bookingRequestDto, Long propertyId, Long userId);
    BookingResponseDto cancelBooking(Long bookingId, Long userId);
    BookingResponseDto getBookingById(Long bookingId, Long userId);
    BookingResponsePage getBookingsByUserId(Long userId, int page, int size);
    BookingResponsePage getBookingsByPropertyId(Long propertyId, Long userId, int page, int size);
    List<BookingDatesDto> getBookedDatesByPropertyId(Long propertyId);
    BookingResponseDto confirmBooking(Long bookingId, Long userId);
    BookingResponsePage getBookingsByHostIdAndStatus(Long hostId, String status, int page, int size);
}
