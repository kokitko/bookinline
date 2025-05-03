package com.bookinline.bookinline.integration.service;

import com.bookinline.bookinline.dto.UserRequestDto;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.entity.enums.UserStatus;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.service.UserService;
import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
public class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Flyway flyway;

    private User testUser = new User();
    private UserRequestDto testUserRequestDto = new UserRequestDto();

    @BeforeEach
    public void setup() {
        flyway.clean();
        flyway.migrate();
        testUser.setFullName("John Doe");
        testUser.setEmail("johndoe88@gmail.com");
        testUser.setPhoneNumber("1234567890");
        testUser.setPassword("password123");
        testUser.setStatus(UserStatus.ACTIVE);
        testUser.setRole(Role.GUEST);
        testUser = userRepository.save(testUser);

        testUserRequestDto.setFullName("John Doe");
        testUserRequestDto.setEmail("johndoe88@gmail.com");
        testUserRequestDto.setPhoneNumber("1234567890");
        testUserRequestDto.setPassword("password123");
    }

    @Test
    public void UserService_SetPhoneNumber_ReturnsUserResponseDto() {
        testUserRequestDto.setPhoneNumber("0987654321");
        userService.setPhoneNumber(testUserRequestDto, testUser.getId());

        User updatedUser = userRepository.findById(1L).orElse(null);
        Assertions.assertThat(updatedUser).isNotNull();
        Assertions.assertThat(updatedUser.getPhoneNumber()).isEqualTo(testUserRequestDto.getPhoneNumber());
    }

    @Test
    public void UserService_SetEmail_ReturnsUserResponseDto() {
        testUserRequestDto.setEmail("janedoe91@gmail.com");
        userService.setEmail(testUserRequestDto, testUser.getId());

        User updatedUser = userRepository.findById(1L).orElse(null);
        Assertions.assertThat(updatedUser).isNotNull();
        Assertions.assertThat(updatedUser.getEmail()).isEqualTo(testUserRequestDto.getEmail());
    }

    @Test
    public void UserService_SetPassword_ReturnsUserResponseDto() {
        testUserRequestDto.setPassword("password456");
        userService.setPassword(testUserRequestDto, testUser.getId());

        User updatedUser = userRepository.findById(1L).orElse(null);
        Assertions.assertThat(updatedUser).isNotNull();
        Assertions.assertThat(passwordEncoder.matches(testUserRequestDto.getPassword(), updatedUser.getPassword())).isTrue();
    }

    @Test
    public void UserService_DeleteUser_ReturnsVoid() {
        userService.deleteUser(testUser.getId());

        User deletedUser = userRepository.findById(testUser.getId()).orElse(null);
        Assertions.assertThat(deletedUser).isNull();
    }
}
