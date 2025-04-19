package com.bookinline.bookinline.dto;

import lombok.Data;

@Data
public class UserRequestDto {
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
}
