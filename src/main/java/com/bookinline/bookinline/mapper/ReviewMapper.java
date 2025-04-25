package com.bookinline.bookinline.mapper;

import com.bookinline.bookinline.dto.ReviewRequestDto;
import com.bookinline.bookinline.dto.ReviewResponseDto;
import com.bookinline.bookinline.dto.ReviewResponsePage;
import com.bookinline.bookinline.entity.Review;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewMapper {
    public static Review mapToReviewEntity(ReviewRequestDto reviewRequestDto) {
        return Review.builder()
                .rating(reviewRequestDto.getRating())
                .comment(reviewRequestDto.getComment())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static ReviewResponseDto mapToReviewResponseDto(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .authorName(review.getAuthor().getFullName())
                .build();
    }

    public static ReviewResponsePage mapToReviewResponsePage(Page<Review> reviewPage,
                                                             List<ReviewResponseDto> reviewResponseDtos) {
        ReviewResponsePage reviewResponsePage = new ReviewResponsePage();
        reviewResponsePage.setPage(reviewPage.getNumber());
        reviewResponsePage.setSize(reviewPage.getSize());
        reviewResponsePage.setTotalElements(reviewPage.getTotalElements());
        reviewResponsePage.setTotalPages(reviewPage.getTotalPages());
        reviewResponsePage.setLast(reviewPage.isLast());
        reviewResponsePage.setReviews(reviewResponseDtos);
        return reviewResponsePage;
    }
}
