package com.bookinline.bookinline.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationRequest(
        @Email
        @NotBlank(message = "Email is required")
        String email,

        @Size(min = 6)
        @NotBlank(message = "Password is required")
        String password) {
}
