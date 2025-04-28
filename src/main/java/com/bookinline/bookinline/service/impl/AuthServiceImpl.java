package com.bookinline.bookinline.service.impl;

import com.bookinline.bookinline.dto.AuthenticationRequest;
import com.bookinline.bookinline.dto.AuthenticationResponse;
import com.bookinline.bookinline.dto.RegisterRequest;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.UserStatus;
import com.bookinline.bookinline.exception.EmailBeingUsedException;
import com.bookinline.bookinline.exception.IllegalRoleException;
import com.bookinline.bookinline.exception.InvalidUserDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.security.JwtService;
import com.bookinline.bookinline.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
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

        String token = jwtService.generateToken(user);
        logger.info("User registered successfully: {}", request.getEmail());
        return new AuthenticationResponse(token);
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
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

        String token = jwtService.generateToken(user);
        logger.info("User logged in successfully: {}", request.getEmail());
        return new AuthenticationResponse(token);
    }
}
