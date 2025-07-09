package com.bookinline.bookinline.service.impl;

import com.bookinline.bookinline.dto.*;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.UserStatus;
import com.bookinline.bookinline.exception.EmailBeingUsedException;
import com.bookinline.bookinline.exception.IllegalRoleException;
import com.bookinline.bookinline.exception.InvalidRefreshTokenException;
import com.bookinline.bookinline.exception.InvalidUserDataException;
import io.micrometer.core.annotation.Timed;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.security.JwtService;
import com.bookinline.bookinline.service.AuthService;

import java.util.Arrays;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final boolean isProd;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager,
                           UserDetailsService userDetailsService,
                           Environment env) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.isProd = Arrays.asList(env.getActiveProfiles()).contains("prod");
    }

    @Timed(
            value = "auth.register",
            description = "Time taken to register a user")
    @Override
    public AuthResponse register(RegisterRequest request, HttpServletResponse response) {
        logger.info("Attempting to register user with email: {}", request.getEmail());


        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Email already in use: {}", request.getEmail());
            throw new EmailBeingUsedException("Email already being used");
        }
        if(request.getRole() == Role.ADMIN) {
            logger.warn("Registration failed: Illegal role to register: {}", request.getRole());
            throw new IllegalRoleException("Illegal role to register");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        String accessToken = jwtService.generateToken(user);

        logger.info("Setting refresh token for user: {}", request.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user);
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(isProd)
                .path("/")
                .sameSite("Secure")
                .maxAge(JwtService.REFRESH_TOKEN_VALIDITY / 1000)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        logger.info("User registered successfully: {}", request.getEmail());
        return new AuthResponse(accessToken);
    }

    @Timed(
            value = "auth.login",
            description = "Time taken to login a user")
    @Override
    public AuthResponse login(AuthenticationRequest request, HttpServletResponse response) {
        logger.info("Attempting to login user with email: {}", request.getEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            logger.warn("Authentication failed with email: {}", request.getEmail());
            throw new InvalidUserDataException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->  {
                    logger.warn("Login failed: User with email {} not found", request.getEmail());
                    return new InvalidUserDataException("Invalid email or password");
                });

        String accessToken = jwtService.generateToken(user);

        logger.info("Setting refresh token for user: {}", request.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user);
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(isProd)
                .path("/")
                .sameSite("Secure")
                .maxAge(JwtService.REFRESH_TOKEN_VALIDITY / 1000)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        logger.info("User logged in successfully: {}", request.getEmail());
        return new AuthResponse(accessToken);
    }

    @Timed(
            value = "auth.refreshToken",
            description = "Time taken to refresh a token")
    @Override
    public AuthResponse refreshToken(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || jwtService.isTokenExpired(refreshToken)) {
            logger.warn("Invalid refresh token: {}", refreshToken);
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        String username = jwtService.extractUsername(refreshToken);
        logger.info("Attempting to refresh token for user: {}", username);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String newAccessToken = jwtService.generateToken(userDetails);

        logger.info("Setting new refresh token for user: {}", username);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(isProd)
                .path("/")
                .sameSite("Secure")
                .maxAge(JwtService.REFRESH_TOKEN_VALIDITY / 1000)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        logger.info("Refreshed token successfully for user: {}", username);
        return new AuthResponse(newAccessToken);
    }

    @Timed(
            value = "auth.logout",
            description = "Time taken to logout a user")
    @Override
    public void logout(HttpServletResponse response) {
        logger.info("Logging out user, clearing refresh token cookie");
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(isProd)
                .path("/")
                .sameSite("Secure")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        logger.info("User logged out successfully");
    }
}
