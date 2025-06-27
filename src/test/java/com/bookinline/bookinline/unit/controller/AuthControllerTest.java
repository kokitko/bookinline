package com.bookinline.bookinline.unit.controller;

import com.bookinline.bookinline.controller.AuthController;
import com.bookinline.bookinline.dto.AuthResponse;
import com.bookinline.bookinline.dto.AuthenticationRequest;
import com.bookinline.bookinline.dto.RegisterRequest;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.security.JwtAuthFilter;
import com.bookinline.bookinline.security.JwtService;
import com.bookinline.bookinline.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthService authService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserDetailsService userDetailsService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("New user registration - successful scenario")
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("John Doe", "johndoe88@gmail.com",
                "password123", Role.GUEST);

        AuthResponse response = new AuthResponse("fake-access-jwt-token");

        Mockito.when(authService.register(any(RegisterRequest.class), any(HttpServletResponse.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("fake-access-jwt-token"));
    }

    @Test
    @DisplayName("User login - successful scenario")
    void shouldLoginUserSuccessfully() throws Exception {
        AuthenticationRequest loginRequest = new AuthenticationRequest("johndoe88@gmail.com", "password123");

        AuthResponse response = new AuthResponse("fake-access-jwt-token");

        Mockito.when(authService.login(any(AuthenticationRequest.class), any(HttpServletResponse.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("fake-access-jwt-token"));
    }

    @Test
    @DisplayName("Registration with existing email - error 409")
    void shouldFailRegisterWithInvalidData() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest("","","", Role.GUEST);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login with invalid credentials - error 400")
    void shouldFailLoginWithInvalidData() throws Exception {
        AuthenticationRequest invalidLoginRequest = new AuthenticationRequest("","");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginRequest)))
                .andExpect(status().isBadRequest());
    }
}
