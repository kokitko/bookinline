package com.bookinline.bookinline.service.impl;

import com.bookinline.bookinline.dto.ReviewRequestDto;
import com.bookinline.bookinline.dto.ReviewResponseDto;
import com.bookinline.bookinline.dto.ReviewResponsePage;
import com.bookinline.bookinline.entity.*;
import com.bookinline.bookinline.repository.BookingRepository;
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
        Review review = mapReviewDtoToEntity(reviewRequestDto);

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!hasPersonStayedInProperty(propertyId, userId)) {
            throw new IllegalArgumentException("User has not stayed in this property");
        }

        if (!hasPersonLeftReview(propertyId, userId)) {
            throw new IllegalArgumentException("User has already left a review for this property");
        }

        review.setProperty(property);
        review.setAuthor(user);
        review.setCreatedAt(LocalDateTime.now());

        ReviewResponseDto reviewResponse = mapReviewEntityToDto(reviewRepository.save(review));

        property.setAverageRating(calculateAverageRating(propertyId));
        propertyRepository.save(property);

        return reviewResponse;
    }

    @Override
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        reviewRepository.delete(review);
        Property property = review.getProperty();
        property.setAverageRating(calculateAverageRating(property.getId()));
        propertyRepository.save(property);
    }

    @Override
    public ReviewResponsePage getReviewsByPropertyId(Long propertyId, int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Review> reviewPage = reviewRepository.findByPropertyId(propertyId, pageable);
        List<ReviewResponseDto> reviewResponseDtos = reviewPage.getContent()
                .stream()
                .map(this::mapReviewEntityToDto)
                .toList();

        ReviewResponsePage reviewResponsePage = new ReviewResponsePage();
        reviewResponsePage.setPage(reviewPage.getNumber());
        reviewResponsePage.setSize(reviewPage.getSize());
        reviewResponsePage.setTotalElements(reviewPage.getTotalElements());
        reviewResponsePage.setTotalPages(reviewPage.getTotalPages());
        reviewResponsePage.setLast(reviewPage.isLast());
        reviewResponsePage.setReviews(reviewResponseDtos);
        return reviewResponsePage;
    }

    @Override
    public ReviewResponsePage getReviewsByUserId(Long userId, int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Review> reviewPage = reviewRepository.findByAuthorId(userId, pageable);
        List<ReviewResponseDto> reviewResponseDtos = reviewPage.getContent()
                .stream()
                .map(this::mapReviewEntityToDto)
                .toList();

        ReviewResponsePage reviewResponsePage = new ReviewResponsePage();
        reviewResponsePage.setPage(reviewPage.getNumber());
        reviewResponsePage.setSize(reviewPage.getSize());
        reviewResponsePage.setTotalElements(reviewPage.getTotalElements());
        reviewResponsePage.setTotalPages(reviewPage.getTotalPages());
        reviewResponsePage.setLast(reviewPage.isLast());
        reviewResponsePage.setReviews(reviewResponseDtos);
        return reviewResponsePage;
    }

    @Override
    public double calculateAverageRating(Long propertyId) {
        List<Review> reviews = reviewRepository.findByPropertyId(propertyId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        double totalRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .sum();
        return totalRating / reviews.size();
    }

    private boolean hasPersonStayedInProperty(Long propertyId, Long userId) {
        List<Booking> bookings = bookingRepository.findByPropertyIdAndGuestIdAndStatus
                (propertyId, userId, BookingStatus.CHECKED_OUT);
        return !bookings.isEmpty();
    }

    private boolean hasPersonLeftReview(Long propertyId, Long userId) {
        List<Review> reviews = reviewRepository.findByPropertyIdAndAuthorId(propertyId, userId);
        return !reviews.isEmpty();
    }

    private Review mapReviewDtoToEntity(ReviewRequestDto reviewRequestDto) {
        return Review.builder()
                .rating(reviewRequestDto.getRating())
                .comment(reviewRequestDto.getComment())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private ReviewResponseDto mapReviewEntityToDto(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .authorName(review.getAuthor().getFullName())
                .build();
    }
}
