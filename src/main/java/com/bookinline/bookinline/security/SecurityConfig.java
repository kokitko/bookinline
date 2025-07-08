package com.bookinline.bookinline.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private Environment environment;
    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        boolean isTest = Arrays.asList(environment.getActiveProfiles()).contains("test");
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> {
                    if (isTest) {
                        csrf.disable();
                    } else {
                        CookieCsrfTokenRepository repo = CookieCsrfTokenRepository.withHttpOnlyFalse();
                        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
                        requestHandler.setCsrfRequestAttributeName(null);
                        csrf
                                .csrfTokenRequestHandler(requestHandler)
                                .csrfTokenRepository(repo)
                                .ignoringRequestMatchers("/api/auth/refresh-token");
                    }
    })
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/properties/filter",
                                "/api/properties/{id}",
                                "/api/properties/available",
                                "/api/reviews/property/{propertyId}",
                                "/api/reviews/user/{userId}",
                                "/api/bookings/property/{propertyId}/dates",
                                "/api/s3/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api/csrf-token").permitAll()
                        .requestMatchers(
                                "/api/reviews/property/{propertyId}/review",
                                "/api/reviews/{reviewId}",
                                "/api/reviews/property/{propertyId}/has-review",
                                "/api/bookings/property/{propertyId}/book",
                                "/api/bookings/{bookingId}/cancel",
                                "/api/bookings/user",
                                "/api/bookings/guest/{status}").hasRole("GUEST")
                        .requestMatchers(
                                "/api/properties/update/{propertyId}",
                                "/api/properties/create",
                                "/api/properties/host",
                                "/api/properties/delete/{propertyId}",
                                "/api/bookings/{bookingId}/confirm",
                                "/api/bookings/property/{propertyId}",
                                "/api/bookings/host").hasRole("HOST")
                        .requestMatchers(
                                "/api/user/**",
                                "/api/bookings/{bookingId}").authenticated()
                        .requestMatchers("/api/admin/**",
                                "/actuator/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
