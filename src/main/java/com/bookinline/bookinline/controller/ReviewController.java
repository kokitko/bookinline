package com.bookinline.bookinline.controller;

import com.bookinline.bookinline.dto.ReviewRequestDto;
import com.bookinline.bookinline.dto.ReviewResponseDto;
import com.bookinline.bookinline.dto.ReviewResponsePage;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.exception.ErrorObject;
import com.bookinline.bookinline.exception.UnauthorizedActionException;
import com.bookinline.bookinline.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Review", description = "Endpoints for managing reviews")
public class ReviewController {
    private final ReviewService reviewService;
    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PreAuthorize("hasRole('ROLE_GUEST')")
    @PostMapping("/property/{propertyId}")
    @Operation(summary = "Leave a review for a property",
            description = "Leave a review for a property with the given ID, requires guest role",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Review added successfully"),
                    @ApiResponse(responseCode = "400", description = "User already reviewed this property/Invalid review data",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
                    @ApiResponse(responseCode = "403", description = "User does not have permission to review a property",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
                    @ApiResponse(responseCode = "404", description = "User/Property not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
            }
    )
    public ResponseEntity<ReviewResponseDto> addReview(@PathVariable Long propertyId,
                                                       @RequestBody @Valid ReviewRequestDto reviewRequestDto) {
        Long userId = getAuthenticatedUserId();
        ReviewResponseDto reviewResponseDto = reviewService.addReview(propertyId, userId, reviewRequestDto);
        return ResponseEntity.ok(reviewResponseDto);
    }

    @PreAuthorize("hasRole('ROLE_GUEST')")
    @GetMapping("/{reviewId}")
    @Operation(summary = "Get review by ID",
            description = "Get review by ID, requires guest role",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Review retrieved successfully"),
                    @ApiResponse(responseCode = "403", description = "User does not have permission to review a property",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
                    @ApiResponse(responseCode = "404", description = "User/Review not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
            }
    )
    public ResponseEntity<ReviewResponseDto> getReview(@PathVariable Long reviewId) {
        Long userId = getAuthenticatedUserId();
        ReviewResponseDto reviewResponseDto = reviewService.getReviewById(reviewId, userId);
        return ResponseEntity.ok(reviewResponseDto);
    }

    @PreAuthorize("hasRole('ROLE_GUEST')")
    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Delete a review",
            description = "Delete a review with the given ID, requires guest role",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
                    @ApiResponse(responseCode = "403", description = "User does not have permission to review a property",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
                    @ApiResponse(responseCode = "404", description = "User/Review not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
            }
    )
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        Long userId = getAuthenticatedUserId();
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/property/{propertyId}")
    @Operation(summary = "Get reviews for a property",
            description = "Get reviews for a property with the given ID, does not require authentication",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of reviews retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Property not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
            }
    )
    public ResponseEntity<ReviewResponsePage> getReviewsByPropertyId(@PathVariable Long propertyId,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        ReviewResponsePage reviewResponsePage = reviewService.getReviewsByPropertyId(propertyId, page, size);
        return ResponseEntity.ok(reviewResponsePage);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get reviews for a user",
            description = "Get reviews for a user with the given ID, does not require authentication",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of reviews retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
            }
    )
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
