package com.bookinline.bookinline.integration.security;

import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.exception.UserNotFoundException;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.security.CustomUserDetailsService;
import com.bookinline.bookinline.service.S3Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class CustomUserDetailsServiceTest {
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private S3Service s3Service;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_ShouldReturnUser_WhenUserExists() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@example.com");

        assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
    }

    @Test
    void loadUserByUsername_ShouldThrow_WhenUserDoesNotExist() {
        when(userRepository.findByEmail("notfound@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("notfound@example.com");
        });
    }
}