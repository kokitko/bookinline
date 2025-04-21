package com.bookinline.bookinline.controller;

import com.bookinline.bookinline.dto.ReviewRequestDto;
import com.bookinline.bookinline.dto.ReviewResponseDto;
import com.bookinline.bookinline.dto.ReviewResponsePage;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.exception.UnauthorizedActionException;
import com.bookinline.bookinline.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/property/{propertyId}")
    public ResponseEntity<ReviewResponseDto> addReview(@PathVariable Long propertyId,
                                                       @RequestBody ReviewRequestDto reviewRequestDto) {
        Long userId = getAuthenticatedUserId();
        ReviewResponseDto reviewResponseDto = reviewService.addReview(propertyId, userId, reviewRequestDto);
        return ResponseEntity.ok(reviewResponseDto);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<ReviewResponsePage> getReviewsByPropertyId(@PathVariable Long propertyId,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        ReviewResponsePage reviewResponsePage = reviewService.getReviewsByPropertyId(propertyId, page, size);
        return ResponseEntity.ok(reviewResponsePage);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ReviewResponsePage> getReviewsByUserId(@PathVariable Long userId,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        ReviewResponsePage reviewResponsePage = reviewService.getReviewsByUserId(userId, page, size);
        return ResponseEntity.ok(reviewResponsePage);
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((User) principal).getId();
            }
        }
        throw new UnauthorizedActionException("Authentication object is null");
    }
}
