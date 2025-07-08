package com.bookinline.bookinline.integration.security;

import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.security.JwtService;
import com.bookinline.bookinline.service.S3Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {
    @Autowired
    private JwtService jwtService;
    @MockBean
    private S3Service s3Service;

    @Test
    void generateTokenAndValidate_ShouldWorkCorrectly() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(Role.GUEST);

        String token = jwtService.generateToken(user);

        assertThat(jwtService.isTokenValid(token, user)).isTrue();
        assertThat(jwtService.extractUsername(token)).isEqualTo("test@example.com");
    }
}
