package com.bookinline.bookinline.unit.mapper;

import com.bookinline.bookinline.dto.UserResponseDto;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.entity.enums.UserStatus;
import com.bookinline.bookinline.mapper.UserMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {
    private final UserMapper userMapper = new UserMapper();

    @Test
    void shouldMapToUserDto() {
        User user = new User(1L, "johndoe@gmail.com",
                "password123", "John Doe", "1234567890", UserStatus.ACTIVE,
                "Active user", Role.GUEST, null, null);

        UserResponseDto userDto = userMapper.mapToUserResponseDto(user);

        assertThat(userDto).isNotNull();
        assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
        assertThat(userDto.getFullName()).isEqualTo(user.getFullName());
        assertThat(userDto.getPhoneNumber()).isEqualTo(user.getPhoneNumber());
        assertThat(userDto.getStatus()).isEqualTo(user.getStatus().name());
        assertThat(userDto.getStatusDescription()).isEqualTo(user.getStatusDescription());
    }
}
