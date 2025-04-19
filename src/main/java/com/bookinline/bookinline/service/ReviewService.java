package com.bookinline.bookinline.service;

import com.bookinline.bookinline.dto.ReviewRequestDto;
import com.bookinline.bookinline.dto.ReviewResponseDto;
import com.bookinline.bookinline.dto.ReviewResponsePage;

public interface ReviewService {
    ReviewResponseDto addReview(Long propertyId, Long userId, ReviewRequestDto reviewRequestDto);
    void deleteReview(Long reviewId);
    ReviewResponsePage getReviewsByPropertyId(Long propertyId, int page, int size);
    ReviewResponsePage getReviewsByUserId(Long userId, int page, int size);
    double calculateAverageRating(Long propertyId);
}
