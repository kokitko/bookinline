package com.bookinline.bookinline.controller;

import com.bookinline.bookinline.dto.BookingResponseDto;
import com.bookinline.bookinline.dto.PropertyResponseDto;
import com.bookinline.bookinline.dto.UserResponseDto;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.exception.UnauthorizedActionException;
import com.bookinline.bookinline.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private AdminService adminService;
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PutMapping("/warn/{userId}")
    public ResponseEntity<UserResponseDto> warnUser(@PathVariable Long userId,
                                                    @RequestParam String reason) {
        Long adminId = getAuthenticatedAdminId();
        UserResponseDto userResponseDto = adminService.warnUser(userId, reason, adminId);
        return ResponseEntity.ok(userResponseDto);
    }

    @DeleteMapping("/ban/{userId}")
    public ResponseEntity<UserResponseDto> banUser(@PathVariable Long userId,
                                                   @RequestParam String reason) {
        Long adminId = getAuthenticatedAdminId();
        UserResponseDto userResponseDto = adminService.banUser(userId, reason, adminId);
        return ResponseEntity.ok(userResponseDto);
    }

    @PutMapping("/unban/{userId}")
    public ResponseEntity<UserResponseDto> unbanUser(@PathVariable Long userId,
                                                     @RequestParam String reason) {
        Long adminId = getAuthenticatedAdminId();
        UserResponseDto userResponseDto = adminService.unbanUser(userId, reason, adminId);
        return ResponseEntity.ok(userResponseDto);
    }

    @DeleteMapping("/property/{propertyId}")
    public ResponseEntity<PropertyResponseDto> deleteProperty(@RequestParam Long propertyId) {
        Long adminId = getAuthenticatedAdminId();
        adminService.deleteProperty(propertyId, adminId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/booking/{bookingId}")
    public ResponseEntity<BookingResponseDto> cancelBooking(@RequestParam Long bookingId) {
        Long adminId = getAuthenticatedAdminId();
        BookingResponseDto bookingResponseDto = adminService.cancelBooking(bookingId, adminId);
        return ResponseEntity.ok(bookingResponseDto);
    }

    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<Void> deleteReview(@RequestParam Long reviewId) {
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
