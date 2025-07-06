package com.bookinline.bookinline.service;

import com.bookinline.bookinline.dto.*;

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
    UserResponsePage getAllUsers(int page, int size, Long adminId);
    PropertyResponsePage getAllProperties(int page, int size, Long adminId);
    BookingResponsePage getAllBookings(int page, int size, Long adminId);
    ReviewResponsePage getAllReviews(int page, int size, Long adminId);
    UserResponsePage getUsersByStatus(String status, int page, int size, Long adminId);
    PropertyResponsePage getPropertiesByPropertyType(String type, int page, int size, Long adminId);
    BookingResponsePage getBookingsByStatus(String status, int page, int size, Long adminId);
}
