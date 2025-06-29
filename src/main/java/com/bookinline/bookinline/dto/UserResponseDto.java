package com.bookinline.bookinline.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private String email;
    private String fullName;
    private String phoneNumber;
    private String status;
    private String statusDescription;
    private String role;
}
