package com.bookinline.bookinline.mapper;

import com.bookinline.bookinline.dto.UserResponseDto;
import com.bookinline.bookinline.dto.UserResponsePage;
import com.bookinline.bookinline.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

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

    public static UserResponsePage mapToUserResponsePage(Page<User> userPage) {
        UserResponsePage responsePage = new UserResponsePage();
        responsePage.setPage(userPage.getNumber());
        responsePage.setSize(userPage.getSize());
        responsePage.setTotalElements(userPage.getTotalElements());
        responsePage.setTotalPages(userPage.getTotalPages());
        responsePage.setLast(userPage.isLast());

        List<UserResponseDto> userDtos = userPage.getContent().stream()
                .map(UserMapper::mapToUserResponseDto)
                .toList();

        responsePage.setUsers(userDtos);
        return responsePage;
    }
}
