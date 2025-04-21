package com.bookinline.bookinline.controller;

import com.bookinline.bookinline.dto.BookingRequestDto;
import com.bookinline.bookinline.dto.BookingResponseDto;
import com.bookinline.bookinline.dto.BookingResponsePage;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;
    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/property/{propertyId}")
    public ResponseEntity<BookingResponseDto> bookProperty(@RequestBody BookingRequestDto bookingRequestDto,
                                                          @PathVariable Long propertyId) {
        Long userId = getAuthenticatedUserId();
        BookingResponseDto bookingResponse = bookingService.bookProperty(bookingRequestDto, propertyId, userId);
        return ResponseEntity.ok(bookingResponse);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> cancelBooking(@PathVariable Long bookingId) {
        BookingResponseDto bookingResponse = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(bookingResponse);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<BookingResponsePage> getBookingsByUserId(@PathVariable Long userId,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
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

    @PostMapping("/{bookingId}/confirm")
    public ResponseEntity<BookingResponseDto> confirmBooking(@PathVariable Long bookingId) {
        BookingResponseDto bookingResponse = bookingService.confirmBooking(bookingId);
        return ResponseEntity.ok(bookingResponse);
    }

    @PostMapping("/{bookingId}/check-out")
    public ResponseEntity<BookingResponseDto> checkOutBooking(@PathVariable Long bookingId) {
        BookingResponseDto bookingResponse = bookingService.checkOutBooking(bookingId);
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
        throw new RuntimeException("Authentication object is null");
    }
}
