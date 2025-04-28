package com.bookinline.bookinline.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationRequest {
        @Email
        @NotBlank(message = "Email is required")
        String email;

        @Size(min = 6, max = 20)
        @NotBlank(message = "Password is required")
        String password;
}
