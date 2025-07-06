package com.bookinline.bookinline.service.impl;

import com.bookinline.bookinline.dto.*;
import com.bookinline.bookinline.entity.Booking;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.Review;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.BookingStatus;
import com.bookinline.bookinline.entity.enums.PropertyType;
import com.bookinline.bookinline.entity.enums.UserStatus;
import com.bookinline.bookinline.exception.BookingNotFoundException;
import com.bookinline.bookinline.exception.PropertyNotFoundException;
import com.bookinline.bookinline.exception.ReviewNotFoundException;
import com.bookinline.bookinline.exception.UserNotFoundException;
import com.bookinline.bookinline.mapper.BookingMapper;
import com.bookinline.bookinline.mapper.PropertyMapper;
import com.bookinline.bookinline.mapper.ReviewMapper;
import com.bookinline.bookinline.mapper.UserMapper;
import com.bookinline.bookinline.repository.BookingRepository;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.repository.ReviewRepository;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.service.AdminService;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Timed(
            value = "admin.getAllUsers",
            description = "Time taken to get all users by admin")
    @Override
    public UserResponseDto getUserById(Long userId, Long adminId) {
        logger.info("Admin {} is fetching user with ID {}", adminId, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return UserMapper.mapToUserResponseDto(user);
    }

    @Timed(
            value = "admin.getPropertyById",
            description = "Time taken to get property by admin")
    @Override
    public PropertyResponseDto getPropertyById(Long propertyId, Long adminId) {
        logger.info("Admin {} is fetching property with ID {}", adminId, propertyId);
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found"));
        return PropertyMapper.mapToPropertyResponseDto(property);
    }

    @Timed(
            value = "admin.getBookingById",
            description = "Time taken to get booking by admin")
    @Override
    public BookingResponseDto getBookingById(Long bookingId, Long adminId) {
        logger.info("Admin {} is fetching booking with ID {}", adminId, bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));
        return BookingMapper.mapToBookingResponseDto(booking);
    }

    @Timed(
            value = "admin.getReviewById",
            description = "Time taken to get review by admin")
    @Override
    public ReviewResponseDto getReviewById(Long reviewId, Long adminId) {
        logger.info("Admin {} is fetching review with ID {}", adminId, reviewId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));
        return ReviewMapper.mapToReviewResponseDto(review);
    }

    @Timed(
            value = "admin.warnUser",
            description = "Time taken to warn user by admin")
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

    @Timed(
            value = "admin.banUser",
            description = "Time taken to ban user by admin")
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

    @Timed(
            value = "admin.unbanUser",
            description = "Time taken to unban user by admin")
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

    @Timed(
            value = "admin.changePropertyAvailability",
            description = "Time taken to change property availability by admin")
    @Override
    public PropertyResponseDto changePropertyAvailability(Long propertyId, Long adminId) {
        logger.info("Admin {} is deactivating property {}", adminId, propertyId);
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found"));
        property.setAvailable(!property.getAvailable());
        return PropertyMapper.mapToPropertyResponseDto(property);
    }

    @Timed(
            value = "admin.cancelBooking",
            description = "Time taken to cancel booking by admin")
    @Override
    public BookingResponseDto cancelBooking(Long bookingId, Long adminId) {
        logger.info("Admin {} is cancelling booking {}", adminId, bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new UserNotFoundException("Booking not found"));
        booking.setStatus(BookingStatus.CANCELLED);
        return BookingMapper.mapToBookingResponseDto(booking);
    }

    @Timed(
            value = "admin.deleteReview",
            description = "Time taken to delete review by admin")
    @Override
    public void deleteReview(Long reviewId, Long adminId) {
        logger.info("Admin {} is deleting review {}", adminId, reviewId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));
        reviewRepository.delete(review);
        calculateAverageRating(review.getProperty().getId());
        logger.info("Average rating updated for property {}", review.getProperty().getId());
    }

    @Timed(
            value = "admin.getAllUsers",
            description = "Time taken to get all users by admin")
    @Override
    public UserResponsePage getAllUsers(int page, int size, Long adminId) {
        logger.info("Admin {} is fetching all users with pagination: page {}, size {}", adminId, page, size);
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<User> users = userRepository.findAll(pageable);
        UserResponsePage userResponsePage = UserMapper.mapToUserResponsePage(users);
        logger.info("Total users found: {}", userResponsePage.getTotalElements());
        return userResponsePage;
    }

    @Timed(
            value = "admin.getAllProperties",
            description = "Time taken to get all properties by admin")
    @Override
    public PropertyResponsePage getAllProperties(int page, int size, Long adminId) {
        logger.info("Admin {} is fetching all properties with pagination: page {}, size {}", adminId, page, size);
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Property> properties = propertyRepository.findAll(pageable);
        PropertyResponsePage propertyResponsePage = PropertyMapper.mapToPropertyResponsePage(properties);
        logger.info("Total properties found: {}", propertyResponsePage.getTotalElements());
        return propertyResponsePage;
    }

    @Timed(
            value = "admin.getAllBookings",
            description = "Time taken to get all bookings by admin")
    @Override
    public BookingResponsePage getAllBookings(int page, int size, Long adminId) {
        logger.info("Admin {} is fetching all bookings with pagination: page {}, size {}", adminId, page, size);
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Booking> bookings = bookingRepository.findAll(pageable);
        BookingResponsePage bookingResponsePage = BookingMapper.mapToBookingResponsePage(bookings);
        logger.info("Total bookings found: {}", bookingResponsePage.getTotalElements());
        return bookingResponsePage;
    }

    @Timed(
            value = "admin.getAllReviews",
            description = "Time taken to get all reviews by admin")
    @Override
    public ReviewResponsePage getAllReviews(int page, int size, Long adminId) {
        logger.info("Admin {} is fetching all reviews with pagination: page {}, size {}", adminId, page, size);
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Review> reviews = reviewRepository.findAll(pageable);
        ReviewResponsePage reviewResponsePage = ReviewMapper.mapToReviewResponsePage(reviews);
        logger.info("Total reviews found: {}", reviewResponsePage.getTotalElements());
        return reviewResponsePage;
    }

    @Timed(
            value = "admin.getUsersByStatus",
            description = "Time taken to get users by status")
    @Override
    public UserResponsePage getUsersByStatus(String status, int page, int size, Long adminId) {
        logger.info("Admin {} is fetching users with status {} with pagination: page {}, size {}", adminId, status, page, size);
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        UserStatus userStatus = UserStatus.valueOf(status.toUpperCase());
        Page<User> users = userRepository.findUsersByStatus(userStatus, pageable);
        UserResponsePage userResponsePage = UserMapper.mapToUserResponsePage(users);
        logger.info("Total users found with status {}: {}", status, userResponsePage.getTotalElements());
        return userResponsePage;
    }

    @Timed(
            value = "admin.getPropertiesByTypeAndAvailability",
            description = "Time taken to get properties by type and availability")
    @Override
    public PropertyResponsePage getPropertiesByPropertyType(String type, int page, int size, Long adminId) {
        logger.info("Admin {} is fetching properties of type {} with pagination: page {}, size {}", adminId, type, page, size);
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        PropertyType propertyType = PropertyType.valueOf(type.toUpperCase());
        Page<Property> properties = propertyRepository.findPropertiesByPropertyType(propertyType, pageable);
        PropertyResponsePage propertyResponsePage = PropertyMapper.mapToPropertyResponsePage(properties);
        logger.info("Total properties found of type {}: {}", type, propertyResponsePage.getTotalElements());
        return propertyResponsePage;
    }

    @Timed(
            value = "admin.getBookingsByStatus",
            description = "Time taken to get bookings by status")
    @Override
    public BookingResponsePage getBookingsByStatus(String status, int page, int size, Long adminId) {
        logger.info("Admin {} is fetching bookings with status {} with pagination: page {}, size {}", adminId, status, page, size);
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
        Page<Booking> bookings = bookingRepository.findBookingsByStatus(bookingStatus, pageable);
        BookingResponsePage bookingResponsePage = BookingMapper.mapToBookingResponsePage(bookings);
        logger.info("Total bookings found with status {}: {}", status, bookingResponsePage.getTotalElements());
        return bookingResponsePage;
    }

    @Timed(
            value = "admin.calculateAverageRating",
            description = "Time taken to calculate average rating inside admin service")
    private double calculateAverageRating(Long propertyId) {
        logger.info("Calculating average rating for property with ID: {}", propertyId);

        List<Review> reviews = reviewRepository.findByPropertyId(propertyId);
        if (reviews.isEmpty()) {
            logger.info("No reviews found for property with ID: {}", propertyId);
            return 0.0;
        }
        double totalRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .sum();
        double averageRating = totalRating / reviews.size();
        logger.info("Average rating for property with ID: {} is {}", propertyId, averageRating);

        return averageRating;
    }
}
