package com.bookinline.bookinline.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    @Email
    @NotBlank(message = "Email is required")
    private String email;

    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phoneNumber;
}
