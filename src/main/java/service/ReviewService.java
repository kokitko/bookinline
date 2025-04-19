package service;

import dto.ReviewRequestDto;
import dto.ReviewResponseDto;
import dto.ReviewResponsePage;

import java.util.List;

public interface ReviewService {
    ReviewResponseDto addReview(Long propertyId, Long userId, ReviewRequestDto reviewRequestDto);
    void deleteReview(Long reviewId);
    ReviewResponsePage getReviewsByPropertyId(Long propertyId, int page, int size);
    ReviewResponsePage getReviewsByUserId(Long userId, int page, int size);
    double calculateAverageRating(Long propertyId);
}
