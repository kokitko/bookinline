package com.bookinline.bookinline.integration.service;

import com.bookinline.bookinline.dto.AuthenticationRequest;
import com.bookinline.bookinline.dto.AuthenticationResponse;
import com.bookinline.bookinline.dto.RegisterRequest;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.entity.enums.UserStatus;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.service.AuthService;
import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthServiceIntegrationTest {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Flyway flyway;

    User guest = new User();
    RegisterRequest registerRequest = new RegisterRequest(
            "Jane Doe", "janedoe91@gmail.com", "password456", Role.HOST);

    @BeforeEach
    public void setup() {
        flyway.clean();
        flyway.migrate();
        guest.setFullName("John Doe");
        guest.setEmail("johndoe88@gmail.com");
        guest.setPassword(passwordEncoder.encode("password123"));
        guest.setPhoneNumber("1234567890");
        guest.setStatus(UserStatus.ACTIVE);
        guest.setRole(Role.GUEST);
        guest = userRepository.save(guest);
    }

    @Test
    public void AuthService_Register_ReturnsAuthenticationResponse() {
        AuthenticationResponse response = authService.register(registerRequest);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getToken()).isNotNull();
    }

    @Test
    public void AuthService_Login_ReturnsAuthenticationResponse() {
        String guestPassword = "password123";
        AuthenticationRequest request = new AuthenticationRequest(
                guest.getEmail(), guestPassword);

        AuthenticationResponse response = authService.login(request);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getToken()).isNotNull();
    }
}
