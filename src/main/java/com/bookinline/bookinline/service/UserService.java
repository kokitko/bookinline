package com.bookinline.bookinline.service;

import com.bookinline.bookinline.dto.UserRequestDto;
import com.bookinline.bookinline.dto.UserResponseDto;

public interface UserService {
    UserResponseDto setPhoneNumber(UserRequestDto userRequestDto, Long userId);
    UserResponseDto setEmail(UserRequestDto userRequestDto, Long userId);
    UserResponseDto setPassword(UserRequestDto userRequestDto, Long userId);
    UserResponseDto getUserById(Long userId);
    UserResponseDto getUserById(Long userId, Long authenticatedUserId);
    void deleteUser(Long userId);
}
