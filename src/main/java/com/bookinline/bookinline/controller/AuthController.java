package com.bookinline.bookinline.controller;

import com.bookinline.bookinline.dto.*;
import com.bookinline.bookinline.exception.ErrorObject;
import com.bookinline.bookinline.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import com.bookinline.bookinline.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Registration and login endpoints")
public class AuthController {
    private final AuthService authService;
    private JwtService jwtService;
    private UserDetailsService userDetailsService;

    @Autowired
    public AuthController(AuthService authService, JwtService jwtService, UserDetailsService userDetailsService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user",
            description = """
                    Detailed description of the registration process:
                    1. The user sends a POST request to the /register endpoint with their details.
                    2. The server validates the request data.
                    3. If the data is valid, the server creates a new user in the database.
                    4. The server generates a access token and a refresh token for the user.
                    5. The server sets the refresh token as a cookie in the response.
                    6. The server responds with a success message and the user's access token.
                    7. If the data is invalid or the user already exists, the server responds with an error message.
                    """, responses = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
            @ApiResponse(responseCode = "409", description = "User already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
    })
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request,
                                                 HttpServletResponse response) {
        return ResponseEntity.ok(authService.register(request, response));
    }

    @PostMapping("/login")
    @Operation(summary = "Login an existing user",
            description = """
                    Detailed description of the login process:
                    1. The user sends a POST request to the /login endpoint with their credentials.
                    2. The server validates the request data.
                    3. If the credentials are valid, the server generates a access and refresh tokens for the user.
                    4. The server sets the refresh token as a cookie in the response.
                    5. The server responds with a success message and the user's access token.
                    6. If the credentials are invalid, the server responds with an error message.
                    """, responses = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
    })
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthenticationRequest request,
                                              HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(request, response));
    }


    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh user JWT token",
            description = """
                    Detailed description of the token refresh process:
                    1. The user sends a POST request to the /refresh-token endpoint.
                    2. The server validates the refresh token from the cookies.
                    3. If the refresh token is valid, the server generates a new jwtTokens for the user.
                    4. The server sets the new refresh token as a cookie in the response.
                    5. The server responds with the new access token.
                    6. If the refresh token is invalid or expired, the server responds with an error message.
                    """, responses = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or expired refresh token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
    })
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        System.out.println("Received refresh token: " + refreshToken);
        AuthResponse authResponse = authService.refreshToken(refreshToken, response);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user",
            description = """
                    Detailed description of the logout process:
                    1. The user sends a POST request to the /logout endpoint.
                    2. The server invalidates the user's refresh token.
                    3. The server clears the refresh token cookie in the response.
                    4. The server responds with a noContent message.
                    """, responses = {
            @ApiResponse(responseCode = "200", description = "User logged out successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
    })
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.noContent().build();
    }
}
