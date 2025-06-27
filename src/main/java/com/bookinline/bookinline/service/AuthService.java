package com.bookinline.bookinline.service;

import com.bookinline.bookinline.dto.AuthResponse;
import com.bookinline.bookinline.dto.AuthenticationRequest;
import com.bookinline.bookinline.dto.RegisterRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request, HttpServletResponse response);
    AuthResponse login(AuthenticationRequest request, HttpServletResponse response);
    AuthResponse refreshToken(String refreshToken, HttpServletResponse response);
    void logout(HttpServletResponse response);
}
