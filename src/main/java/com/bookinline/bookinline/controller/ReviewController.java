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
            description = """
                    Detailed description of review creation process:
                    - **Endpoint**: `/api/reviews/property/{propertyId}`
                    - **Method**: `POST`
                    - **Request Body**: JSON object containing review details
                    - **Path Variable**: `propertyId` - ID of the property to review
                    
                    1. Guest sends a POST request to the endpoint with the property ID and review body.
                    1.1. The system validates the review data (rating, comment, etc.).
                    2. The system checks if property exists and if the user has permission to review it
                    (person has to stay in the property at least once, looks for bookings with status 'CHECKED_OUT').
                    3. Checks if the user has already reviewed the property.
                    4. Updates property rating based on the new review.
                    5. If all checks pass, the review is saved in the database and user gets review response.
                    """,
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
            description = """
                    Detailed description of review retrieval process:
                    - **Endpoint**: `/api/reviews/{reviewId}`
                    - **Method**: `GET`
                    - **Path Variable**: `reviewId` - ID of the review to retrieve
                    
                    1. Guest sends a GET request to the endpoint with the review ID.
                    2. The system checks if the review exists and if the user has permission to view it
                    (only author can get complete info about review).
                    3. If all checks pass, the review is retrieved from the database and returned in the response.
                    """,
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
            description = """
                    Detailed description of review deletion process:
                    - **Endpoint**: `/api/reviews/{reviewId}`
                    - **Method**: `DELETE`
                    - **Path Variable**: `reviewId` - ID of the review to delete
                    
                    1. Guest sends a DELETE request to the endpoint with the review ID.
                    2. The system checks if the review exists and if the user has permission to delete it
                    (only author can delete the review).
                    3. Updates property rating based on the deleted review.
                    4. If all checks pass, the review is deleted from the database and a noContent response is returned.
                    """,
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
            description = """
                    Detailed description of property reviews retrieval process:
                    - **Endpoint**: `/api/reviews/property/{propertyId}`
                    - **Method**: `GET`
                    - **Path Variable**: `propertyId` - ID of the property to retrieve reviews for
                    
                    1. Any user sends a GET request to the endpoint with the property ID.
                    2. The system checks if the property exists.
                    3. If exists, the system retrieves all reviews for the property and returns them in a paginated format.
                    """,
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
            description = """
                    Detailed description of user reviews retrieval process:
                    - **Endpoint**: `/api/reviews/user/{userId}`
                    - **Method**: `GET`
                    - **Path Variable**: `userId` - ID of the user to retrieve reviews for
                    
                    1. Any user sends a GET request to the endpoint with the user ID.
                    2. The system checks if the user exists.
                    3. If exists, the system retrieves all reviews for the user and returns them in a paginated format.
                    """,
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

    @GetMapping("/property/{propertyId}/has-review")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    @Operation(summary = "Check if user has left a review for a property",
            description = """
                    Detailed description of user review check process:
                    - **Endpoint**: `/api/reviews/property/{propertyId}/has-review`
                    - **Method**: `GET`
                    - **Path Variables**: `propertyId` - ID of the property
                    
                    1. Guest sends a GET request to the endpoint with the property ID and user ID.
                    2. The system checks if the user has left a review for the specified property.
                    3. Returns true if the user has left a review, false otherwise.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Review existence check successful"),
                    @ApiResponse(responseCode = "404", description = "Property or User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
            }
    )
    public ResponseEntity<Boolean> hasUserLeftReviewForProperty(@PathVariable Long propertyId) {
        Long userId = getAuthenticatedUserId();
        boolean hasLeftReview = reviewService.hasPersonLeftReview(propertyId, userId);
        return ResponseEntity.ok(hasLeftReview);
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
