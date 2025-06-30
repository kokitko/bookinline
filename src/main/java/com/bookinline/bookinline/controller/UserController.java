package com.bookinline.bookinline.controller;

import com.bookinline.bookinline.dto.PasswordDto;
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
@Tag(name = "User", description = "User management API, all endpoints require authentication")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/phone")
    @Operation(summary = "Set a phone number",
            description = """
                    Detailed description of the phone number setting process:
                    1. The user sends a PUT request to the /phone endpoint with userRequestDto info.
                    2. The server validates the request data.
                    3. The server retrieves the authenticated user's ID from the security context.
                    4. The server updates the user's phone number in the database.
                    5. The server returns a response with the updated user information.
                    """,
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
            description = """
                    Detailed description of the email changing process:
                    1. The user sends a PUT request to the /email endpoint with userRequestDto info.
                    2. The server validates the request data.
                    3. The server retrieves the authenticated user's ID from the security context.
                    4. The server checks if the email already exists in the database.
                    5. If the email exists, the server returns a conflict error.
                    6. If the email does not exist, the server updates the user's email in the database.
                    7. The server returns a response with the updated user information.
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email changed successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
                    @ApiResponse(responseCode = "409", description = "Email already exists",
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
            description = """
                    Detailed description of the password changing process:
                    1. The user sends a PUT request to the /password endpoint with userRequestDto info.
                    2. The server validates the request data.
                    3. The server retrieves the authenticated user's ID from the security context.
                    4. The server updates the user's password in the database.
                    5. The server returns a response with the updated user information.
                    """,
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
            description = """
                    Detailed description of the current user retrieval process:
                    1. The user sends a GET request to the /me endpoint.
                    2. The server retrieves the authenticated user's ID from the security context.
                    3. The server fetches the user's information from the database.
                    4. The server returns a response with the user's information.
                    """,
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
            description = """
                    Detailed description of the user retrieval process by ID:
                    1. The user sends a GET request to the /{userId} endpoint.
                    2. The server retrieves the authenticated user's ID from the security context.
                    3. The server checks if the authenticated user is authorized to view the requested user
                    (by checking if guest has booked a property from host user).
                    4. If authorized, the server fetches the user's information from the database.
                    5. The server returns a response with the user's information.
                    6. If not authorized, the server returns a forbidden error.
                    """,
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
            description = """
                    Detailed description of the user deletion process:
                    1. The user sends a DELETE request to the / endpoint with their password.
                    2. The server retrieves the authenticated user's ID from the security context.
                    3. The server checks if the user exists in the database.
                    4. If the user exists and password matches, the server deletes the user from the database.
                    5. The server returns a response indicating successful deletion (noContent).
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
            }
    )
    public ResponseEntity<Void> deleteUser(@RequestBody PasswordDto passwordDto) {
        Long userId = getAuthenticatedUserId();
        userService.deleteUser(userId, passwordDto.getPassword());
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
