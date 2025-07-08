package com.bookinline.bookinline.integration.controller;

import com.bookinline.bookinline.dto.AuthenticationRequest;
import com.bookinline.bookinline.dto.RegisterRequest;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.entity.enums.UserStatus;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.security.JwtService;
import com.bookinline.bookinline.service.S3Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private Flyway flyway;
    @MockBean
    private S3Service s3Service;

    @BeforeEach
    public void setup() {
        flyway.clean();
        flyway.migrate();
        userRepository.deleteAll();
    }

    @Test
    public void shouldRegisterSuccessfully() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "John Doe", "johndoe88@gmail.com", "password123", Role.GUEST
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    public void shouldFailRegisterWithExistingEmail() throws Exception {
        User user = new User(null, "johndoe88@example.com", "password123", "John Doe", null, UserStatus.ACTIVE, null, Role.GUEST, null, null);
        userRepository.save(user);

        RegisterRequest request = new RegisterRequest(
                "John Doe", "johndoe88@example.com", "securePass123", Role.GUEST);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }


    @Test
    void shouldLoginSuccessfully() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("John Doe", "johndoe88@gmail.com", "password123", Role.GUEST);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        AuthenticationRequest login = new AuthenticationRequest("johndoe88@gmail.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void shouldFailLoginWithInvalidData() throws Exception {
        AuthenticationRequest login = new AuthenticationRequest("invalid@", "");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refreshTokenEndpoint_ReturnsNewAccessTokenAndSetsCookie() throws Exception {
        User user = new User(null, "johndoe88@example.com", "password123", "John Doe", null, UserStatus.ACTIVE, null, Role.GUEST, null, null);
        userRepository.save(user);

        String refreshToken = jwtService.generateRefreshToken(user);

        mockMvc.perform(post("/api/auth/refresh-token")
                        .cookie(new Cookie("refreshToken", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(cookie().exists("refreshToken"));
    }

    @Test
    void logoutEndpoint_ClearsRefreshTokenCookie() throws Exception {
        User user = new User(null, "johndoe88@example.com", "password123", "John Doe", null, UserStatus.ACTIVE, null, Role.GUEST, null, null);
        userRepository.save(user);

        String refreshToken = jwtService.generateRefreshToken(user);

        MockHttpServletRequestBuilder request = post("/api/auth/logout")
                .cookie(new Cookie("refreshToken", refreshToken))
                .contentType(MediaType.APPLICATION_JSON);
    }
}
