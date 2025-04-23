package com.bookinline.bookinline.controller;

import com.bookinline.bookinline.dto.BookingRequestDto;
import com.bookinline.bookinline.dto.BookingResponseDto;
import com.bookinline.bookinline.dto.BookingResponsePage;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.exception.ErrorObject;
import com.bookinline.bookinline.exception.UnauthorizedActionException;
import com.bookinline.bookinline.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Booking", description = "Endpoints for managing bookings")
public class BookingController {
    private final BookingService bookingService;
    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PreAuthorize("hasRole('ROLE_GUEST')")
    @PostMapping("/property/{propertyId}")
    @Operation(summary = "Book a property",
            description = "Book a property with the given ID, requires guest role",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Property created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid booking dates/Property not available",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
                    @ApiResponse(responseCode = "403", description = "User does not have permission to book a property",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
                    @ApiResponse(responseCode = "404", description = "User/Property not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
            }
    )
    public ResponseEntity<BookingResponseDto> bookProperty(@RequestBody BookingRequestDto bookingRequestDto,
                                                          @PathVariable Long propertyId) {
        Long userId = getAuthenticatedUserId();
        BookingResponseDto bookingResponse = bookingService.bookProperty(bookingRequestDto, propertyId, userId);
        return ResponseEntity.ok(bookingResponse);
    }

    @PreAuthorize("hasRole('ROLE_GUEST')")
    @DeleteMapping("/{bookingId}")
    @Operation(summary = "Cancel a booking",
            description = "Cancel a booking with the given ID, requires guest role",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Booking canceled successfully"),
                    @ApiResponse(responseCode = "403", description = "User does not have permission to cancel a property",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
                    @ApiResponse(responseCode = "404", description = "User/Property/Booking not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
            }
    )
    public ResponseEntity<BookingResponseDto> cancelBooking(@PathVariable Long bookingId) {
        Long userId = getAuthenticatedUserId();
        BookingResponseDto bookingResponse = bookingService.cancelBooking(bookingId, userId);
        return ResponseEntity.ok(bookingResponse);
    }

    @PreAuthorize("hasRole('ROLE_GUEST')")
    @GetMapping("/user")
    @Operation(summary = "Get bookings for authenticated user",
            description = "Get bookings for authenticated user, requires guest role",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of bookings retrieved successfully")
            }
    )
    public ResponseEntity<BookingResponsePage> getBookingsByUserId(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        Long userId = getAuthenticatedUserId();
        BookingResponsePage bookingResponsePage = bookingService.getBookingsByUserId(userId, page, size);
        return ResponseEntity.ok(bookingResponsePage);
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<BookingResponsePage> getBookingsByPropertyId(@PathVariable Long propertyId,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        BookingResponsePage bookingResponsePage = bookingService.getBookingsByPropertyId(propertyId, page, size);
        return ResponseEntity.ok(bookingResponsePage);
    }

    @PreAuthorize("hasRole('ROLE_HOST')")
    @PostMapping("/{bookingId}/confirm")
    @Operation(summary = "Confirm a booking",
            description = "Confirm a booking with the given ID, requires host role",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Booking confirmed successfully"),
                    @ApiResponse(responseCode = "403", description = "User does not have permission to cancel a property | " +
                            "Booking is already cancelled/confirmed",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
                    @ApiResponse(responseCode = "404", description = "User/Property/Booking not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
            }
    )
    public ResponseEntity<BookingResponseDto> confirmBooking(@PathVariable Long bookingId) {
        Long userId = getAuthenticatedUserId();
        BookingResponseDto bookingResponse = bookingService.confirmBooking(bookingId, userId);
        return ResponseEntity.ok(bookingResponse);
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
