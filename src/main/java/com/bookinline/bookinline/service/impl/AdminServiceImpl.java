package com.bookinline.bookinline.service.impl;

import com.bookinline.bookinline.dto.BookingResponseDto;
import com.bookinline.bookinline.dto.UserResponseDto;
import com.bookinline.bookinline.entity.Booking;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.Review;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.BookingStatus;
import com.bookinline.bookinline.entity.enums.UserStatus;
import com.bookinline.bookinline.exception.PropertyNotFoundException;
import com.bookinline.bookinline.exception.ReviewNotFoundException;
import com.bookinline.bookinline.exception.UserNotFoundException;
import com.bookinline.bookinline.mapper.BookingMapper;
import com.bookinline.bookinline.mapper.UserMapper;
import com.bookinline.bookinline.repository.BookingRepository;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.repository.ReviewRepository;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.service.AdminService;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(AdminServiceImpl.class);

    private UserRepository userRepository;
    private PropertyRepository propertyRepository;
    private ReviewRepository reviewRepository;
    private BookingRepository bookingRepository;
    public AdminServiceImpl(UserRepository userRepository,
                             PropertyRepository propertyRepository,
                             ReviewRepository reviewRepository,
                             BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public UserResponseDto warnUser(Long userId, String reason, Long adminId) {
        logger.info("Admin {} is warning user {} for reason: {}", adminId, userId, reason);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setStatus(UserStatus.WARNED);
        user.setStatusDescription(reason);
        userRepository.save(user);
        return UserMapper.mapToUserResponseDto(user);
    }

    @Override
    public UserResponseDto banUser(Long userId, String reason, Long adminId) {
        logger.info("Admin {} is banning user {} for reason: {}", adminId, userId, reason);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setStatus(UserStatus.BANNED);
        user.setStatusDescription(reason);
        userRepository.save(user);
        return UserMapper.mapToUserResponseDto(user);
    }

    @Override
    public UserResponseDto unbanUser(Long userId, String reason, Long adminId) {
        logger.info("Admin {} is unbanning user {} for reason: {}", adminId, userId, reason);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setStatus(UserStatus.ACTIVE);
        user.setStatusDescription(reason);
        userRepository.save(user);
        return UserMapper.mapToUserResponseDto(user);
    }

    @Override
    public void deleteProperty(Long propertyId, Long adminId) {
        logger.info("Admin {} is deleting property {}", adminId, propertyId);
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found"));
        propertyRepository.delete(property);
    }

    @Override
    public BookingResponseDto cancelBooking(Long bookingId, Long adminId) {
        logger.info("Admin {} is cancelling booking {}", adminId, bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new UserNotFoundException("Booking not found"));
        booking.setStatus(BookingStatus.CANCELLED);
        return BookingMapper.mapToBookingResponseDto(booking);
    }

    @Override
    public void deleteReview(Long reviewId, Long adminId) {
        logger.info("Admin {} is deleting review {}", adminId, reviewId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));
        reviewRepository.delete(review);
    }
}
