package com.bookinline.bookinline.controller;

import com.bookinline.bookinline.dto.AuthenticationRequest;
import com.bookinline.bookinline.dto.AuthenticationResponse;
import com.bookinline.bookinline.dto.RegisterRequest;
import com.bookinline.bookinline.exception.ErrorObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bookinline.bookinline.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Registration and login endpoints")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user",
            description = """
                    Detailed description of the registration process:
                    1. The user sends a POST request to the /register endpoint with their details.
                    2. The server validates the request data.
                    3. If the data is valid, the server creates a new user in the database.
                    4. The server responds with a success message and the user's jwtToken.
                    5. If the data is invalid or the user already exists, the server responds with an error message.
                    """, responses = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
            @ApiResponse(responseCode = "409", description = "User already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
    })
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login an existing user",
            description = """
                    Detailed description of the login process:
                    1. The user sends a POST request to the /login endpoint with their credentials.
                    2. The server validates the request data.
                    3. If the credentials are valid, the server generates a jwtToken for the user.
                    4. The server responds with a success message and the user's jwtToken.
                    5. If the credentials are invalid, the server responds with an error message.
                    6. The user can use the jwtToken for subsequent requests to access protected resources.
                    """, responses = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
    })
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
