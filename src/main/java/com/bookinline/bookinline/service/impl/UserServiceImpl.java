package com.bookinline.bookinline.service.impl;

import com.bookinline.bookinline.dto.UserRequestDto;
import com.bookinline.bookinline.dto.UserResponseDto;
import com.bookinline.bookinline.entity.enums.BookingStatus;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.exception.EmailBeingUsedException;
import com.bookinline.bookinline.exception.UnauthorizedActionException;
import com.bookinline.bookinline.exception.UserNotFoundException;
import com.bookinline.bookinline.mapper.UserMapper;
import com.bookinline.bookinline.repository.BookingRepository;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.service.UserService;
import io.micrometer.core.annotation.Timed;
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

    @Timed(
            value = "user.setPhoneNumber",
            description = "Time taken to set phone number")
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
        return UserMapper.mapToUserResponseDto(user);
    }

    @Timed(
            value = "user.setEmail",
            description = "Time taken to set email")
    @Override
    public UserResponseDto setEmail(UserRequestDto userRequestDto, Long userId) {
        logger.info("Setting email for user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", userId);
                    return new UserNotFoundException("User not found");
                });
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            logger.error("Email {} already exists", userRequestDto.getEmail());
            throw new EmailBeingUsedException("Email already exists");
        }
        user.setEmail(userRequestDto.getEmail());
        userRepository.save(user);
        return UserMapper.mapToUserResponseDto(user);
    }

    @Timed(
            value = "user.setPassword",
            description = "Time taken to set password")
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
        return UserMapper.mapToUserResponseDto(user);
    }

    @Timed(
            value = "user.setFullName",
            description = "Time taken to set full name")
    @Override
    public UserResponseDto getUserById(Long userId) {
        logger.info("Fetching user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", userId);
                    return new UserNotFoundException("User not found");
                });
        return UserMapper.mapToUserResponseDto(user);
    }

    @Timed(
            value = "user.getUserById",
            description = "Time taken to get user by ID")
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
        return UserMapper.mapToUserResponseDto(user);
    }

    @Timed(
            value = "user.deleteUser",
            description = "Time taken to delete user")
    @Override
    public void deleteUser(Long userId, String password) {
        logger.info("Deleting user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", userId);
                    return new UserNotFoundException("User not found");
                });
        if (encoder.matches(password, user.getPassword())) {
            userRepository.delete(user);
        } else {
            logger.error("Password mismatch for user ID: {}", userId);
            throw new UnauthorizedActionException("Incorrect password");
        }
    }
}
