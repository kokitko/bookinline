package com.bookinline.bookinline.mapper;

import com.bookinline.bookinline.dto.UserResponseDto;
import com.bookinline.bookinline.entity.User;

public class UserMapper {
    public static UserResponseDto mapToUserResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .fullName(user.getFullName())
                .status(String.valueOf(user.getStatus()))
                .statusDescription(user.getStatusDescription())
                .role(String.valueOf(user.getRole()))
                .build();
    }
}
