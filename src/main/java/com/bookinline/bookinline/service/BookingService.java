package com.bookinline.bookinline.service;

import com.bookinline.bookinline.dto.BookingRequestDto;
import com.bookinline.bookinline.dto.BookingResponseDto;
import com.bookinline.bookinline.dto.BookingResponsePage;

public interface BookingService {
    BookingResponseDto bookProperty(BookingRequestDto bookingRequestDto);
    BookingResponseDto cancelBooking(Long bookingId);
    BookingResponsePage getBookingsByUserId(Long userId, int page, int size);
    BookingResponsePage getBookingsByPropertyId(Long propertyId, int page, int size);
    boolean isPropertyAvailable(Long propertyId, String startDate, String endDate);
}
