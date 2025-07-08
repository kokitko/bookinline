package com.bookinline.bookinline.integration.service;

import com.bookinline.bookinline.dto.AuthResponse;
import com.bookinline.bookinline.dto.AuthenticationRequest;
import com.bookinline.bookinline.dto.RegisterRequest;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.entity.enums.UserStatus;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.security.JwtService;
import com.bookinline.bookinline.service.AuthService;
import com.bookinline.bookinline.service.S3Service;
import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
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
    private JwtService jwtService;
    @Autowired
    private Flyway flyway;
    @MockBean
    private S3Service s3Service;

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
    public void AuthService_Register_ReturnsAuthResponseDto() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthResponse authResponse = authService.register(registerRequest, response);

        Assertions.assertThat(authResponse).isNotNull();
        Assertions.assertThat(authResponse.getAccessToken()).isNotNull();

        String setCookieHeader = response.getHeader("Set-Cookie");
        Assertions.assertThat(setCookieHeader).isNotNull();
        Assertions.assertThat(setCookieHeader).contains("refreshToken")
                .contains("HttpOnly")
                .contains("Secure")
                .contains("SameSite=Secure");

    }

    @Test
    public void AuthService_Login_ReturnsAuthenticationResponse() {
        String guestPassword = "password123";
        AuthenticationRequest request = new AuthenticationRequest(
                guest.getEmail(), guestPassword);

        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthResponse authResponse = authService.login(request, response);

        Assertions.assertThat(authResponse).isNotNull();
        Assertions.assertThat(authResponse.getAccessToken()).isNotNull();

        String setCookieHeader = response.getHeader("Set-Cookie");
        Assertions.assertThat(setCookieHeader).isNotNull();
        Assertions.assertThat(setCookieHeader).contains("refreshToken")
                .contains("HttpOnly")
                .contains("Secure")
                .contains("SameSite=Secure");
    }

    @Test
    public void AuthService_RefreshToken_ReturnsAuthResponse() {
        String refreshToken = jwtService.generateRefreshToken(guest);
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthResponse authResponse = authService.refreshToken(refreshToken, response);

        Assertions.assertThat(authResponse).isNotNull();
        Assertions.assertThat(authResponse.getAccessToken()).isNotNull();

        String setCookieHeader = response.getHeader("Set-Cookie");
        Assertions.assertThat(setCookieHeader).isNotNull();
        Assertions.assertThat(setCookieHeader).contains("refreshToken")
                .contains("HttpOnly")
                .contains("Secure")
                .contains("SameSite=Secure");
    }

    @Test
    public void AuthService_Logout_ClearsRefreshTokenCookie() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        authService.logout(response);

        String setCookieHeader = response.getHeader("Set-Cookie");
        Assertions.assertThat(setCookieHeader).isNotNull();
        Assertions.assertThat(setCookieHeader).contains("refreshToken=;")
                .contains("Max-Age=0")
                .contains("Path=/")
                .contains("HttpOnly")
                .contains("Secure")
                .contains("SameSite=Secure");
    }
}
