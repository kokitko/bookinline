package com.bookinline.bookinline.controller;

import com.bookinline.bookinline.dto.BookingResponseDto;
import com.bookinline.bookinline.dto.PropertyResponseDto;
import com.bookinline.bookinline.dto.ReviewResponseDto;
import com.bookinline.bookinline.dto.UserResponseDto;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.exception.ErrorObject;
import com.bookinline.bookinline.exception.UnauthorizedActionException;
import com.bookinline.bookinline.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin API", description = "Endpoints for managing admin actions, requires admin role")
public class AdminController {
    private AdminService adminService;
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get user details",
            description = """
                    Detailed information about method:
                    - **Endpoint**: `/api/admin/users/{userId}`
                    - **Method**: `GET`
                    - **Path Variable**: `userId` (Long) - ID of the user to retrieve
                    
                    1. Checks authenticated admin ID.
                    2. Checks if user exists.
                    3. Returns user details.
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User details retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
            }
    )
    public ResponseEntity<UserResponseDto> getUserDetails(@PathVariable Long userId) {
        Long adminId = getAuthenticatedAdminId();
        UserResponseDto userResponseDto = adminService.getUserById(userId, adminId);
        return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping("/properties/{propertyId}")
    @Operation(summary = "Get property by ID",
            description = """
                    Detailed information about method:
                    - **Endpoint**: `/api/admin/properties/{propertyId}`
                    - **Method**: `GET`
                    - **Path Variable**: `propertyId` (Long) - ID of the property to retrieve
                    
                    1. Checks authenticated admin ID.
                    2. Checks if property exists.
                    3. Returns property details.
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Property details retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Property not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
            }
    )
    public ResponseEntity<PropertyResponseDto> getPropertyById(@PathVariable Long propertyId) {
        Long adminId = getAuthenticatedAdminId();
        PropertyResponseDto propertyResponseDto = adminService.getPropertyById(propertyId, adminId);
        return ResponseEntity.ok(propertyResponseDto);
    }

    @GetMapping("/bookings/{bookingId}")
    @Operation(summary = "Get booking by ID",
            description = """
                    Detailed information about method:
                    - **Endpoint**: `/api/admin/bookings/{bookingId}`
                    - **Method**: `GET`
                    - **Path Variable**: `bookingId` (Long) - ID of the booking to retrieve
                    
                    1. Checks authenticated admin ID.
                    2. Checks if booking exists.
                    3. Returns booking details.
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Booking details retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Booking not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
            }
    )
    public ResponseEntity<BookingResponseDto> getBookingById(@PathVariable Long bookingId) {
        Long adminId = getAuthenticatedAdminId();
        BookingResponseDto bookingResponseDto = adminService.getBookingById(bookingId, adminId);
        return ResponseEntity.ok(bookingResponseDto);
    }

    @GetMapping("/reviews/{reviewId}")
    @Operation(summary = "Get review by ID",
            description = """
                    Detailed information about method:
                    - **Endpoint**: `/api/admin/reviews/{reviewId}`
                    - **Method**: `GET`
                    - **Path Variable**: `reviewId` (Long) - ID of the review to retrieve
                    
                    1. Checks authenticated admin ID.
                    2. Checks if review exists.
                    3. Returns review details.
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Review details retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Review not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
            }
    )
    public ResponseEntity<ReviewResponseDto> getReviewById(@PathVariable Long reviewId) {
        Long adminId = getAuthenticatedAdminId();
        ReviewResponseDto reviewResponseDto = adminService.getReviewById(reviewId, adminId);
        return ResponseEntity.ok(reviewResponseDto);
    }

    @PutMapping("/warn/{userId}")
    @Operation(summary = "Warn a user",
            description = """
                    Detailed information of user warning:
                    - **Endpoint**: `/api/admin/warn/{userId}`
                    - **Method**: `PUT`
                    - **Path Variable**: `userId` (Long) - ID of the user to warn
                    - **Request Parameter**: `reason` (String) - Reason for warning
                    
                    1. Checks authenticated admin ID.
                    2. Checks if user exists.
                    3. Sets user status to WARNED.
                    4. Sets user status description to the reason.
                    5. Saves user to the database.
                    6. Returns user details.
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                @ApiResponse(responseCode = "200", description = "User warned successfully"),
                @ApiResponse(responseCode = "404", description = "User not found",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
            }
    )
    public ResponseEntity<UserResponseDto> warnUser(@PathVariable Long userId,
                                                    @RequestParam String reason) {
        Long adminId = getAuthenticatedAdminId();
        UserResponseDto userResponseDto = adminService.warnUser(userId, reason, adminId);
        return ResponseEntity.ok(userResponseDto);
    }

    @DeleteMapping("/ban/{userId}")
    @Operation(summary = "Ban a user",
            description = """
                    Detailed information of user banning:
                    - **Endpoint**: `/api/admin/ban/{userId}`
                    - **Method**: `DELETE`
                    - **Path Variable**: `userId` (Long) - ID of the user to ban
                    - **Request Parameter**: `reason` (String) - Reason for banning
                    
                    1. Checks authenticated admin ID.
                    2. Checks if user exists.
                    3. Sets user status to BANNED.
                    4. Sets user status description to the reason.
                    5. Saves user to the database.
                    6. Returns user details.
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User banned successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
            }
    )
    public ResponseEntity<UserResponseDto> banUser(@PathVariable Long userId,
                                                   @RequestParam String reason) {
        Long adminId = getAuthenticatedAdminId();
        UserResponseDto userResponseDto = adminService.banUser(userId, reason, adminId);
        return ResponseEntity.ok(userResponseDto);
    }

    @PutMapping("/unban/{userId}")
    @Operation(summary = "Unban a user",
            description = """
                    Detailed information of user unbanning:
                    - **Endpoint**: `/api/admin/unban/{userId}`
                    - **Method**: `PUT`
                    - **Path Variable**: `userId` (Long) - ID of the user to unban
                    - **Request Parameter**: `reason` (String) - Reason for unbanning
                    
                    1. Checks authenticated admin ID.
                    2. Checks if user exists.
                    3. Sets user status to ACTIVE.
                    4. Sets user status description to the reason.
                    5. Saves user to the database.
                    6. Returns user details.
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User unbanned successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
            }
    )
    public ResponseEntity<UserResponseDto> unbanUser(@PathVariable Long userId,
                                                     @RequestParam String reason) {
        Long adminId = getAuthenticatedAdminId();
        UserResponseDto userResponseDto = adminService.unbanUser(userId, reason, adminId);
        return ResponseEntity.ok(userResponseDto);
    }

    @PutMapping("/property/{propertyId}")
    @Operation(summary = "Change property availability",
            description = """
                    Detailed information of property availability change:
                    - **Endpoint**: `/api/admin/property/{propertyId}`
                    - **Method**: `PUT`
                    - **Path Variable**: `propertyId` (Long) - ID of the property to change availability
                    
                    1. Checks authenticated admin ID.
                    2. Checks if property exists.
                    3. Changes property availability (so guests are not able to book this property).
                    4. Returns property details.
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Property availability changed successfully"),
                    @ApiResponse(responseCode = "404", description = "Property not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
            }
    )
    public ResponseEntity<PropertyResponseDto> changePropertyAvailability(@PathVariable Long propertyId) {
        Long adminId = getAuthenticatedAdminId();
        PropertyResponseDto response = adminService.changePropertyAvailability(propertyId, adminId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/booking/{bookingId}")
    @Operation(summary = "Cancel a booking",
            description = """
                    Detailed information of booking cancellation:
                    - **Endpoint**: `/api/admin/booking/{bookingId}`
                    - **Method**: `DELETE`
                    - **Path Variable**: `bookingId` (Long) - ID of the booking to cancel
                    
                    1. Checks authenticated admin ID.
                    2. Checks if booking exists.
                    3. Cancels the booking.
                    4. Returns booking details.
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Booking cancelled successfully"),
                    @ApiResponse(responseCode = "404", description = "Booking not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
            }
    )
    public ResponseEntity<BookingResponseDto> cancelBooking(@PathVariable Long bookingId) {
        Long adminId = getAuthenticatedAdminId();
        BookingResponseDto bookingResponseDto = adminService.cancelBooking(bookingId, adminId);
        return ResponseEntity.ok(bookingResponseDto);
    }

    @DeleteMapping("/review/{reviewId}")
    @Operation(summary = "Delete a review",
            description = """
                    Detailed information of review deletion:
                    - **Endpoint**: `/api/admin/review/{reviewId}`
                    - **Method**: `DELETE`
                    - **Path Variable**: `reviewId` (Long) - ID of the review to delete
                    
                    1. Checks authenticated admin ID.
                    2. Checks if review exists.
                    3. Deletes the review.
                    4. Returns no content.
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Review deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Review not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
            }
    )
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        Long adminId = getAuthenticatedAdminId();
        adminService.deleteReview(reviewId, adminId);
        return ResponseEntity.noContent().build();
    }

    private Long getAuthenticatedAdminId() {
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
