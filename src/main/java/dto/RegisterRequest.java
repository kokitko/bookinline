package dto;

import entity.Role;

public record RegisterRequest(String fullName, String email, String password, Role role) {
}
