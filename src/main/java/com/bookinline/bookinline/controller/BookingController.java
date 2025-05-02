package com.bookinline.bookinline.controller;

import com.bookinline.bookinline.dto.BookingDatesDto;
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
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            description = """
                    Detailed description of the booking process.
                    - **Endpoint**: `/api/bookings/property/{propertyId}`
                    - **Method**: `POST`
                    - **Request Body**: `BookingRequestDto` containing booking details
                    
                    1. The user sends a booking request with the property ID.
                    1.1. The system validates the booking request.
                    2. The system checks if booking range is valid.
                    3. The system checks if property is available for this date range.
                    4. The system creates a booking and returns the booking details.
                    """,
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
    public ResponseEntity<BookingResponseDto> bookProperty(@RequestBody @Valid BookingRequestDto bookingRequestDto,
                                                          @PathVariable Long propertyId) {
        Long userId = getAuthenticatedUserId();
        BookingResponseDto bookingResponse = bookingService.bookProperty(bookingRequestDto, propertyId, userId);
        return ResponseEntity.ok(bookingResponse);
    }

    @PreAuthorize("hasRole('ROLE_GUEST')")
    @DeleteMapping("/{bookingId}")
    @Operation(summary = "Cancel a booking",
            description = """
                    Detailed description of the booking cancellation process.
                    - **Endpoint**: `/api/bookings/{bookingId}`
                    - **Method**: `DELETE`
                    - **Path Variable**: `bookingId` of the booking to be canceled
                    
                    1. The user sends a cancellation request with the booking ID.
                    2. The system checks if the booking exists and is cancellable.
                    3. The system checks if the user has permission to cancel the booking (only for guest that made booking).
                    4. The system cancels the booking and returns the booking details.
                    """,
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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{bookingId}")
    @Operation(summary = "Get booking by ID",
            description = """
                    Detailed description of the booking retrieval process.
                    - **Endpoint**: `/api/bookings/{bookingId}`
                    - **Method**: `GET`
                    - **Path Variable**: `bookingId` of the booking to be retrieved
                    
                    1. The user sends a request with the booking ID.
                    2. The system checks if the booking exists.
                    3. The system checks if the user has permission to view the booking (only for guest && host of this booking).
                    4. The system retrieves the booking details and returns them.
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Booking retrieved successfully"),
                    @ApiResponse(responseCode = "403", description = "User does not have permission to view this booking",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
                    @ApiResponse(responseCode = "404", description = "User/Property/Booking not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
            }
    )
    public ResponseEntity<BookingResponseDto> getBookingById(@PathVariable Long bookingId) {
        Long userId = getAuthenticatedUserId();
        BookingResponseDto bookingResponse = bookingService.getBookingById(bookingId, userId);
        return ResponseEntity.ok(bookingResponse);
    }

    @PreAuthorize("hasRole('ROLE_GUEST')")
    @GetMapping("/user")
    @Operation(summary = "Get bookings for authenticated user",
            description = """
                    Detailed description of the booking page retrieval process.
                    - **Endpoint**: `/api/bookings/user`
                    - **Method**: `GET`
                    - **Query Parameters**: `page` and `size` for pagination
                    
                    1. The user (with guest role) sends a request to retrieve their bookings.
                    2. The server retrieves the authenticated user's ID from the security context.
                    3. The system looks for bookings associated with the user ID.
                    4. The system returns a paginated list of bookings for the user.
                    """,
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

    @PreAuthorize("hasRole('ROLE_HOST')")
    @GetMapping("/property/{propertyId}")
    @Operation(summary = "Get bookings for property",
            description = """
                    Detailed description of the booking page retrieval process.
                    - **Endpoint**: `/api/bookings/property/{propertyId}`
                    - **Method**: `GET`
                    - **Path Variable**: `propertyId` of the property to retrieve bookings for && `page` and `size` for pagination
                    
                    1. The user (with host role) sends a request to retrieve bookings for a specific property.
                    2. The server retrieves the authenticated user's ID from the security context.
                    3. The system checks if the user is the host of the property.
                    4. The system looks for bookings associated with the property ID.
                    5. The system returns a paginated list of bookings for the property.
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of bookings retrieved successfully"),
                    @ApiResponse(responseCode = "403", description =
                            "User does not have permission to view bookings for this property",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
                    @ApiResponse(responseCode = "404", description = "Property not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
            }
    )
    public ResponseEntity<BookingResponsePage> getBookingsByPropertyId(@PathVariable Long propertyId,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        Long userId = getAuthenticatedUserId();
        BookingResponsePage bookingResponsePage = bookingService.getBookingsByPropertyId(propertyId, userId, page, size);
        return ResponseEntity.ok(bookingResponsePage);
    }


    @GetMapping("/property/{propertyId}/dates")
    @Operation(summary = "Get booking dates for property",
            description = """
                    Detailed description of the booking dates retrieval process.
                    - **Endpoint**: `/api/bookings/property/{propertyId}/dates`
                    - **Method**: `GET`
                    - **Path Variable**: `propertyId` of the property to retrieve booking dates for
                    
                    1. Any user sends a request to retrieve booking dates for a specific property.
                    2. The system checks if the property exists.
                    3. The system retrieves the booking dates associated with the property ID and status 'PENDING' or 'CONFIRMED'.
                    4. The system returns a list of booking dates for the property.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of booking dates retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Property not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
            }
    )
    public ResponseEntity<List<BookingDatesDto>> getBookingDatesByPropertyId(@PathVariable Long propertyId) {
        List<BookingDatesDto> bookedDates = bookingService.getBookedDatesByPropertyId(propertyId);
        return ResponseEntity.ok(bookedDates);
    }

    @PreAuthorize("hasRole('ROLE_HOST')")
    @PutMapping("/{bookingId}/confirm")
    @Operation(summary = "Confirm a booking",
            description = """
                    Detailed description of the booking confirmation process.
                    - **Endpoint**: `/api/bookings/{bookingId}/confirm`
                    - **Method**: `PUT`
                    - **Path Variable**: `bookingId` of the booking to be confirmed
                    
                    1. The host sends a confirmation request with the booking ID.
                    2. The system checks if the user has permission to confirm the booking (only for host of this booking).
                    3. The system checks if the booking exists and has 'PENDING' status.
                    4. The system confirms the booking and returns the booking details.
                    """,
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
