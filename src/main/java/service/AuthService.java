package service;

import dto.AuthenticationRequest;
import dto.AuthenticationResponse;
import dto.RegisterRequest;

public interface AuthService {
    public AuthenticationResponse register(RegisterRequest request);
    public AuthenticationResponse login(AuthenticationRequest request);
}
