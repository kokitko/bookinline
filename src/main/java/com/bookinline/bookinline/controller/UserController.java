package com.bookinline.bookinline.controller;

import com.bookinline.bookinline.dto.UserRequestDto;
import com.bookinline.bookinline.dto.UserResponseDto;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.exception.ErrorObject;
import com.bookinline.bookinline.exception.UnauthorizedActionException;
import com.bookinline.bookinline.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User", description = "User management API")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/phone")
    @Operation(summary = "Set a phone number",
            description = "Changes/Sets a phone number for authenticated user, requires authentication",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Changed/Set phone number successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
            }
    )
    public ResponseEntity<UserResponseDto> setPhoneNumber(@RequestBody @Valid UserRequestDto userRequestDto) {
        Long userId = getAuthenticatedUserId();
        UserResponseDto responseDto = userService.setPhoneNumber(userRequestDto, userId);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/email")
    @Operation(summary = "Change email",
            description = "Changes an email for authenticated user, requires authentication",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email changed successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
            }
    )
    public ResponseEntity<UserResponseDto> setEmail(@RequestBody @Valid UserRequestDto userRequestDto) {
        Long userId = getAuthenticatedUserId();
        UserResponseDto responseDto = userService.setEmail(userRequestDto, userId);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/password")
    @Operation(summary = "Change password",
            description = "Changes a password for authenticated user, requires authentication",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password changed successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
            }
    )
    public ResponseEntity<UserResponseDto> setPassword(@RequestBody @Valid UserRequestDto userRequestDto) {
        Long userId = getAuthenticatedUserId();
        UserResponseDto responseDto = userService.setPassword(userRequestDto, userId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user",
            description = "Returns authenticated User info",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User info retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
            }
    )
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        Long userId = getAuthenticatedUserId();
        UserResponseDto responseDto = userService.getUserById(userId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID",
            description = "Returns User info by ID, requires authentication",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User info retrieved successfully"),
                    @ApiResponse(responseCode = "403", description = "User is not authorized to view this user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
            }
    )
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId) {
        Long authenticatedUserId = getAuthenticatedUserId();
        UserResponseDto responseDto = userService.getUserById(userId, authenticatedUserId);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping
    @Operation(summary = "Delete user",
            description = "Deletes the authenticated user, requires authentication",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
            }
    )
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
