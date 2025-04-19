package service.impl;

import dto.ReviewRequestDto;
import dto.ReviewResponseDto;
import dto.ReviewResponsePage;
import entity.Property;
import entity.Review;
import entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import repositories.PropertyRepository;
import repositories.ReviewRepository;
import repositories.UserRepository;
import service.ReviewService;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             PropertyRepository propertyRepository,
                             UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ReviewResponseDto addReview(Long propertyId, Long userId, ReviewRequestDto reviewRequestDto) {
        Review review = mapReviewDtoToEntity(reviewRequestDto);
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        review.setProperty(property);
        review.setAuthor(user);
        review.setCreatedAt(LocalDateTime.now());
        return mapReviewEntityToDto(reviewRepository.save(review));
    }

    @Override
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        reviewRepository.delete(review);
    }

    @Override
    public ReviewResponsePage getReviewsByPropertyId(Long propertyId, int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Review> reviewPage = reviewRepository.findByPropertyId(propertyId);
        List<ReviewResponseDto> reviewResponseDtos = reviewPage.getContent()
                .stream()
                .map(this::mapReviewEntityToDto)
                .toList();

        ReviewResponsePage reviewResponsePage = new ReviewResponsePage();
        reviewResponsePage.setPage(pageable.getPageNumber());
        reviewResponsePage.setSize(pageable.getPageSize());
        reviewResponsePage.setTotalElements(reviewPage.getTotalElements());
        reviewResponsePage.setTotalPages(reviewPage.getTotalPages());
        reviewResponsePage.setLast(reviewPage.isLast());
        reviewResponsePage.setReviews(reviewResponseDtos);
        return reviewResponsePage;
    }

    @Override
    public ReviewResponsePage getReviewsByUserId(Long userId, int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Review> reviewPage = reviewRepository.findByAuthorId(userId);
        List<ReviewResponseDto> reviewResponseDtos = reviewPage.getContent()
                .stream()
                .map(this::mapReviewEntityToDto)
                .toList();

        ReviewResponsePage reviewResponsePage = new ReviewResponsePage();
        reviewResponsePage.setPage(pageable.getPageNumber());
        reviewResponsePage.setSize(pageable.getPageSize());
        reviewResponsePage.setTotalElements(reviewPage.getTotalElements());
        reviewResponsePage.setTotalPages(reviewPage.getTotalPages());
        reviewResponsePage.setLast(reviewPage.isLast());
        reviewResponsePage.setReviews(reviewResponseDtos);
        return reviewResponsePage;
    }

    @Override
    public double calculateAverageRating(Long propertyId) {
        Page<Review> reviewPage = reviewRepository.findByPropertyId(propertyId);
        List<Review> reviews = reviewPage.getContent();
        if (reviews.isEmpty()) {
            return 0.0;
        }
        double totalRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .sum();
        return totalRating / reviews.size();
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
