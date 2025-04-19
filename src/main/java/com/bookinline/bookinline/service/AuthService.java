package com.bookinline.bookinline.service;

import com.bookinline.bookinline.dto.AuthenticationRequest;
import com.bookinline.bookinline.dto.AuthenticationResponse;
import com.bookinline.bookinline.dto.RegisterRequest;

public interface AuthService {
    public AuthenticationResponse register(RegisterRequest request);
    public AuthenticationResponse login(AuthenticationRequest request);
}
