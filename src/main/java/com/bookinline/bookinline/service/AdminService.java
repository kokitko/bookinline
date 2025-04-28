package com.bookinline.bookinline.service;

import com.bookinline.bookinline.dto.BookingResponseDto;
import com.bookinline.bookinline.dto.PropertyResponseDto;
import com.bookinline.bookinline.dto.ReviewResponseDto;
import com.bookinline.bookinline.dto.UserResponseDto;

public interface AdminService {
    UserResponseDto getUserById(Long userId, Long adminId);
    PropertyResponseDto getPropertyById(Long propertyId, Long adminId);
    BookingResponseDto getBookingById(Long bookingId, Long adminId);
    ReviewResponseDto getReviewById(Long reviewId, Long adminId);
    UserResponseDto warnUser(Long userId, String reason, Long adminId);
    UserResponseDto banUser(Long userId, String reason, Long adminId);
    UserResponseDto unbanUser(Long userId, String reason, Long adminId);
    PropertyResponseDto changePropertyAvailability(Long propertyId, Long adminId);
    BookingResponseDto cancelBooking(Long bookingId, Long adminId);
    void deleteReview(Long reviewId, Long adminId);
}
