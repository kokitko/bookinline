package com.bookinline.bookinline.integration.security;

import com.bookinline.bookinline.security.SecurityConfig;
import com.bookinline.bookinline.service.S3Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SecurityConfigTest {
    @Autowired
    private SecurityConfig securityConfig;
    @Autowired
    @Qualifier("filterChain")
    private SecurityFilterChain securityFilterChain;
    @Autowired
    private AuthenticationProvider authenticationProvider;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @MockBean
    private S3Service s3Service;

    @Test
    void contextLoads() {
        assertThat(securityConfig).isNotNull();
        assertThat(securityFilterChain).isNotNull();
        assertThat(authenticationProvider).isNotNull();
        assertThat(authenticationManager).isNotNull();
        assertThat(passwordEncoder).isNotNull();
    }

    @Test
    void passwordEncoderIsBCrypt() {
        assertThat(passwordEncoder).isInstanceOf(PasswordEncoder.class);
    }
}