package service;

import dto.BookingRequestDto;
import dto.BookingResponseDto;
import dto.BookingResponsePage;

public interface BookingService {
    BookingResponseDto bookProperty(BookingRequestDto bookingRequestDto);
    BookingResponseDto cancelBooking(Long bookingId);
    BookingResponsePage getBookingsByUserId(Long userId, int page, int size);
    BookingResponsePage getBookingsByPropertyId(Long propertyId, int page, int size);
    boolean isPropertyAvailable(Long propertyId, String startDate, String endDate);
}
