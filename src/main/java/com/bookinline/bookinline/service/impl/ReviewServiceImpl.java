package com.bookinline.bookinline.service.impl;

import com.bookinline.bookinline.dto.ReviewRequestDto;
import com.bookinline.bookinline.dto.ReviewResponseDto;
import com.bookinline.bookinline.dto.ReviewResponsePage;
import com.bookinline.bookinline.entity.*;
import com.bookinline.bookinline.entity.enums.BookingStatus;
import com.bookinline.bookinline.exception.*;
import com.bookinline.bookinline.mapper.ReviewMapper;
import com.bookinline.bookinline.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.repository.ReviewRepository;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.service.ReviewService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewRepository reviewRepository;
    private final PropertyRepository propertyRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                              PropertyRepository propertyRepository,
                              BookingRepository bookingRepository,
                              UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.propertyRepository = propertyRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ReviewResponseDto addReview(Long propertyId, Long userId, ReviewRequestDto reviewRequestDto) {
        logger.info("Attempting to add review for property with ID: {} by user with ID: {}", propertyId, userId);

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> {
                    logger.error("Property not found with ID: {}", propertyId);
                    return new PropertyNotFoundException("Property not found");
                });
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new UserNotFoundException("User not found");
                });

        if (!hasPersonStayedInProperty(propertyId, userId)) {
            logger.warn("User with ID: {} has not stayed in property with ID: {}", userId, propertyId);
            throw new UnauthorizedActionException("User has not stayed in this property");
        }

        if (hasPersonLeftReview(propertyId, userId)) {
            logger.warn("User with ID: {} has already left a review for property with ID: {}", userId, propertyId);
            throw new OverReviewingException("User has already left a review for this property");
        }

        Review review = ReviewMapper.mapToReviewEntity(reviewRequestDto);
        review.setProperty(property);
        review.setAuthor(user);
        review.setCreatedAt(LocalDateTime.now());

        ReviewResponseDto reviewResponse = ReviewMapper.mapToReviewResponseDto(reviewRepository.save(review));
        logger.info("Review added successfully for property with ID: {} by user with ID: {}", propertyId, userId);

        property.setAverageRating(calculateAverageRating(propertyId));
        propertyRepository.save(property);
        logger.info("Update average rating for property with ID: {}", propertyId);

        return reviewResponse;
    }

    @Override
    public ReviewResponseDto getReviewById(Long reviewId, Long userId) {
        logger.info("Fetching review with ID: {} for user with ID: {}", reviewId, userId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    logger.error("Review not found with ID: {}", reviewId);
                    return new ReviewNotFoundException("Review not found");
                });

        if (!review.getAuthor().getId().equals(userId)) {
            logger.warn("User with ID: {} is not authorized to view review with ID: {}", userId, reviewId);
            throw new UnauthorizedActionException("User is not authorized to view this review");
        }

        ReviewResponseDto reviewResponse = ReviewMapper.mapToReviewResponseDto(review);
        logger.info("Review fetched successfully for property with ID: {} by user with ID: {}", review.getProperty().getId(), userId);

        return reviewResponse;
    }

    @Override
    public void deleteReview(Long reviewId, Long userId) {
        logger.info("Attempting to delete review with ID: {}", reviewId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    logger.error("Review not found with ID: {}", reviewId);
                    return new ReviewNotFoundException("Review not found");
                });

        if (!review.getAuthor().getId().equals(userId)) {
            logger.warn("User with ID: {} is not authorized to delete review with ID: {}", userId, reviewId);
            throw new UnauthorizedActionException("User is not authorized to delete this review");
        }

        reviewRepository.delete(review);
        logger.info("Review with ID: {} deleted successfully", reviewId);

        Property property = review.getProperty();
        property.setAverageRating(calculateAverageRating(property.getId()));
        propertyRepository.save(property);
        logger.info("Update average rating for property with ID: {}", property.getId());
    }

    @Override
    public ReviewResponsePage getReviewsByPropertyId(Long propertyId, int page, int size) {
        logger.info("Fetching reviews for property with ID: {}, page: {}, size: {}", propertyId, page, size);

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        if (propertyRepository.findById(propertyId).isEmpty()) {
            logger.error("Property not found with ID: {}", propertyId);
            throw new PropertyNotFoundException("Property not found");
        }
        Page<Review> reviewPage = reviewRepository.findByPropertyId(propertyId, pageable);

        logger.info("Found {} reviews for property with ID: {}", reviewPage.getTotalElements(), propertyId);

        return ReviewMapper.mapToReviewResponsePage(reviewPage);
    }

    @Override
    public ReviewResponsePage getReviewsByUserId(Long userId, int page, int size) {
        logger.info("Fetching reviews for user with ID: {}, page: {}, size: {}", userId, page, size);

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        if (userRepository.findById(userId).isEmpty()) {
            logger.error("User not found with ID: {}", userId);
            throw new UserNotFoundException("User not found");
        }
        Page<Review> reviewPage = reviewRepository.findByAuthorId(userId, pageable);

        logger.info("Found {} reviews for user with ID: {}", reviewPage.getTotalElements(), userId);

        return ReviewMapper.mapToReviewResponsePage(reviewPage);
    }

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

    private boolean hasPersonStayedInProperty(Long propertyId, Long userId) {
        logger.info("Checking if user with ID: {} has stayed in property with ID: {}", userId, propertyId);
        List<Booking> bookings = bookingRepository.findByPropertyIdAndGuestIdAndStatus
                (propertyId, userId, BookingStatus.CHECKED_OUT);
        boolean hasStayed = !bookings.isEmpty();
        logger.info("User with ID: {} has {}stayed in property with ID: {}",
                userId, hasStayed ? "" : "not ", propertyId);
        return hasStayed;
    }

    private boolean hasPersonLeftReview(Long propertyId, Long userId) {
        logger.info("Checking if user with ID: {} has left a review for property with ID: {}", userId, propertyId);

        List<Review> reviews = reviewRepository.findByPropertyIdAndAuthorId(propertyId, userId);
        boolean hasLeftReview = !reviews.isEmpty();
        logger.info("User with ID: {} has {}left a review for property with ID: {}", userId,
                hasLeftReview ? "" : "not ", propertyId);
        return hasLeftReview;
    }

}
