package com.bookinline.bookinline.dto;

import com.bookinline.bookinline.entity.Role;

public record RegisterRequest(String fullName, String email, String password, Role role) {
}
