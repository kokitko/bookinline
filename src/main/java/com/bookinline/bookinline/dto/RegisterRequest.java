package com.bookinline.bookinline.dto;

import com.bookinline.bookinline.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
        @NotBlank(message = "Full name is required")
        @Size(min = 3, max = 50)
        String fullName;

        @Email
        @NotBlank(message = "Email is required")
        String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
        String password;

        @NotNull(message = "Role is required")
        Role role;
}
