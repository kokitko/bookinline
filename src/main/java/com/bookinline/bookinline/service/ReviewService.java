package com.bookinline.bookinline.service;

import com.bookinline.bookinline.dto.ReviewRequestDto;
import com.bookinline.bookinline.dto.ReviewResponseDto;
import com.bookinline.bookinline.dto.ReviewResponsePage;

public interface ReviewService {
    ReviewResponseDto addReview(Long propertyId, Long userId, ReviewRequestDto reviewRequestDto);
    ReviewResponseDto getReviewById(Long reviewId, Long userId);
    void deleteReview(Long reviewId, Long userId);
    ReviewResponsePage getReviewsByPropertyId(Long propertyId, int page, int size);
    ReviewResponsePage getReviewsByUserId(Long userId, int page, int size);
    boolean hasPersonLeftReview(Long propertyId, Long userId);
}
