package com.bookinline.bookinline.service.impl;

import com.bookinline.bookinline.dto.UserRequestDto;
import com.bookinline.bookinline.dto.UserResponseDto;
import com.bookinline.bookinline.entity.enums.BookingStatus;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.exception.UnauthorizedActionException;
import com.bookinline.bookinline.exception.UserNotFoundException;
import com.bookinline.bookinline.repository.BookingRepository;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private UserServiceImpl(UserRepository userRepository, BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public UserResponseDto setPhoneNumber(UserRequestDto userRequestDto, Long userId) {
        logger.info("Setting phone number for user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", userId);
                    return new UserNotFoundException("User not found");
                });
        user.setPhoneNumber(userRequestDto.getPhoneNumber());
        userRepository.save(user);
        return mapToDto(user);
    }

    @Override
    public UserResponseDto setEmail(UserRequestDto userRequestDto, Long userId) {
        logger.info("Setting email for user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", userId);
                    return new UserNotFoundException("User not found");
                });
        user.setEmail(userRequestDto.getEmail());
        userRepository.save(user);
        return mapToDto(user);
    }

    @Override
    public UserResponseDto setPassword(UserRequestDto userRequestDto, Long userId) {
        logger.info("Setting password for user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", userId);
                    return new UserNotFoundException("User not found");
                });
        user.setPassword(encoder.encode(userRequestDto.getPassword()));
        userRepository.save(user);
        return mapToDto(user);
    }

    @Override
    public UserResponseDto getUserById(Long userId) {
        logger.info("Fetching user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", userId);
                    return new UserNotFoundException("User not found");
                });
        return mapToDto(user);
    }

    @Override
    public UserResponseDto getUserById(Long userId, Long authenticatedUserId) {
        logger.info("Fetching user with ID: {} for authenticated user ID: {}", userId, authenticatedUserId);
        boolean hasBooking = bookingRepository.existsByGuestIdAndHostIdAndStatuses(userId, authenticatedUserId,
                List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED));
        if (!hasBooking) {
            hasBooking = bookingRepository.existsByGuestIdAndHostIdAndStatuses(authenticatedUserId, userId,
                    List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED));
            if (!hasBooking) {
                logger.error("No bookings found for user ID: {}", userId);
                throw new UnauthorizedActionException("No bookings found");
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", userId);
                    return new UserNotFoundException("User not found");
                });
        return mapToDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        logger.info("Deleting user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", userId);
                    return new UserNotFoundException("User not found");
                });
        userRepository.delete(user);
    }

    private UserResponseDto mapToDto(User user) {
        return UserResponseDto.builder()
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .fullName(user.getFullName())
                .status(String.valueOf(user.getStatus()))
                .statusDescription(user.getStatusDescription())
                .build();
    }
}
