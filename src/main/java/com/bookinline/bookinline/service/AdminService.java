package com.bookinline.bookinline.service;

import com.bookinline.bookinline.dto.BookingResponseDto;
import com.bookinline.bookinline.dto.PropertyResponseDto;
import com.bookinline.bookinline.dto.UserResponseDto;

public interface AdminService {
    UserResponseDto warnUser(Long userId, String reason, Long adminId);
    UserResponseDto banUser(Long userId, String reason, Long adminId);
    UserResponseDto unbanUser(Long userId, String reason, Long adminId);
    PropertyResponseDto deactivateProperty(Long propertyId, Long adminId);
    BookingResponseDto cancelBooking(Long bookingId, Long adminId);
    void deleteReview(Long reviewId, Long adminId);
}
