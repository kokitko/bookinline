package com.bookinline.bookinline.controller;

import com.bookinline.bookinline.dto.UserRequestDto;
import com.bookinline.bookinline.dto.UserResponseDto;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.exception.UnauthorizedActionException;
import com.bookinline.bookinline.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/phone")
    public ResponseEntity<UserResponseDto> setPhoneNumber(@RequestBody UserRequestDto userRequestDto) {
        Long userId = getAuthenticatedUserId();
        UserResponseDto responseDto = userService.setPhoneNumber(userRequestDto, userId);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/email")
    public ResponseEntity<UserResponseDto> setEmail(@RequestBody UserRequestDto userRequestDto) {
        Long userId = getAuthenticatedUserId();
        UserResponseDto responseDto = userService.setEmail(userRequestDto, userId);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/password")
    public ResponseEntity<UserResponseDto> setPassword(@RequestBody UserRequestDto userRequestDto) {
        Long userId = getAuthenticatedUserId();
        UserResponseDto responseDto = userService.setPassword(userRequestDto, userId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        Long userId = getAuthenticatedUserId();
        UserResponseDto responseDto = userService.getUserById(userId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId) {
        Long authenticatedUserId = getAuthenticatedUserId();
        UserResponseDto responseDto = userService.getUserById(userId, authenticatedUserId);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser() {
        Long userId = getAuthenticatedUserId();
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
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
