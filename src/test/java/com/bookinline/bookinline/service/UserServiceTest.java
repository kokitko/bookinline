package com.bookinline.bookinline.service;

import com.bookinline.bookinline.dto.UserRequestDto;
import com.bookinline.bookinline.dto.UserResponseDto;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.entity.enums.UserStatus;
import com.bookinline.bookinline.repository.BookingRepository;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.service.impl.UserServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private UserServiceImpl userService;

    private User user = new User();
    private UserRequestDto userRequestDto = new UserRequestDto();

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1L);
        user.setFullName("Jane Doe");
        user.setEmail("janedor91@gmail.com");
        user.setPhoneNumber("0987654321");
        user.setPassword("password456");
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(Role.GUEST);

        userRequestDto.setFullName("John Doe");
        userRequestDto.setEmail("johndoe88@gmail.com");
        userRequestDto.setPhoneNumber("1234567890");
        userRequestDto.setPassword("password123");
    }

    @Test
    public void UserService_GetUserById_ReturnsUserResponseDto() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        UserResponseDto userResponseDto = userService.getUserById(1L);
        Assertions.assertThat(userResponseDto.getFullName()).isEqualTo(user.getFullName());
    }

    @Test
    public void UserService_SecondGetUserById_ReturnsUserResponseDto() {
        when(bookingRepository.existsByGuestIdAndHostIdAndStatuses(
                Mockito.anyLong(), Mockito.anyLong(), Mockito.anyList())).thenReturn(true);
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        UserResponseDto userResponseDto = userService.getUserById(1L, 2L);
        Assertions.assertThat(userResponseDto.getEmail()).isEqualTo(user.getEmail());
    }
}
