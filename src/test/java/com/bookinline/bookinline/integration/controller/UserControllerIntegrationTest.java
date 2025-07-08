package com.bookinline.bookinline.integration.controller;

import com.bookinline.bookinline.dto.AuthenticationRequest;
import com.bookinline.bookinline.dto.PasswordDto;
import com.bookinline.bookinline.dto.UserRequestDto;
import com.bookinline.bookinline.entity.Booking;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.BookingStatus;
import com.bookinline.bookinline.entity.enums.PropertyType;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.entity.enums.UserStatus;
import com.bookinline.bookinline.repository.BookingRepository;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.service.S3Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private Flyway flyway;
    @MockBean
    private S3Service s3Service;

    User user;
    User host;
    Property property;
    Booking booking;
    String token;

    @BeforeEach
    public void setup() throws Exception {
        flyway.clean();
        flyway.migrate();
        userRepository.deleteAll();

        user = new User();
        user.setFullName("John Doe");
        user.setEmail("johndoe88@gmail.com");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(Role.GUEST);
        user.setStatus(UserStatus.ACTIVE);
        user.setPhoneNumber("1234567890");
        user = userRepository.save(user);

        AuthenticationRequest request = new AuthenticationRequest("johndoe88@gmail.com", "password123");
        token = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
        token = token.substring(16, token.length() - 2);
    }

    @Test
    void shouldSetPhoneNumberSuccessfully() throws Exception {
        String newPhoneNumber = "0987654321";
        UserRequestDto userRequestDto = new UserRequestDto("johndoe88@gmail.com", "password123", "John Doe", newPhoneNumber);

        mockMvc.perform(put("/api/user/phone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value(newPhoneNumber));
    }

    @Test
    void shouldFailSetPhoneNumberWithInvalidPhone() throws Exception {
        String newPhoneNumber = "1234567890-098786754214";
        UserRequestDto userRequestDto = new UserRequestDto("johndoe88@gmail.com", "password123", "John Doe", newPhoneNumber);

        mockMvc.perform(put("/api/user/phone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed: phoneNumber: Invalid phone number format"));
    }

    @Test
    void shouldChangeEmailSuccessfully() throws Exception {
        String newEmail = "newemail@example.com";
        UserRequestDto userRequestDto = new UserRequestDto(newEmail, "password123", "John Doe", "1234567890");

        mockMvc.perform(put("/api/user/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(newEmail));
    }

    @Test
    void shouldFailChangeEmailWithInvalidEmail() throws Exception {
        String newEmail = "invalid-email";
        UserRequestDto userRequestDto = new UserRequestDto(newEmail, "password123", "John Doe", "1234567890");

        mockMvc.perform(put("/api/user/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed: email: must be a well-formed email address"));
    }

    @Test
    void shouldChangePasswordSuccessfully() throws Exception {
        String newPassword = "password456";
        UserRequestDto userRequestDto = new UserRequestDto("johndoe88@gmail.com", newPassword, "John Doe", "1234567890");

        mockMvc.perform(put("/api/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User changedUser = userRepository.findById(user.getId()).orElse(null);
        Assertions.assertThat(passwordEncoder.matches(newPassword, changedUser.getPassword())).isTrue();
    }

    @Test
    void shouldFailChangePasswordWithInvalidPassword() throws Exception {
        String newPassword = "short";
        UserRequestDto userRequestDto = new UserRequestDto("johndoe88@gmail.com", newPassword, "John Doe", "1234567890");

        mockMvc.perform(put("/api/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed: password: Password must be between 6 and 20 characters"));
    }

    @Test
    void shouldReturnUserDetailsSuccessfully() throws Exception {
        mockMvc.perform(get("/api/user/me")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.fullName").value(user.getFullName()))
                .andExpect(jsonPath("$.phoneNumber").value(user.getPhoneNumber()));
    }

    @Test
    void shouldReturnUserDetailsForOtherUserSuccessfully() throws Exception {
        host = new User(null,"janedoe91@gmail.com","password456","Jane Doe",
                null,UserStatus.ACTIVE,null,Role.HOST,null,null);
        host = userRepository.save(host);

        property = new Property(null,"Test title","Test description", "Test City", PropertyType.APARTMENT, 100, 2,"Test address",
                new BigDecimal(100.0),3,true,0.0,host,null,null,null);
        property = propertyRepository.save(property);

        booking = new Booking(null, LocalDate.now(),LocalDate.now(),user,property,BookingStatus.CONFIRMED);
        booking = bookingRepository.save(booking);

        mockMvc.perform(get(String.format("/api/user/%d", booking.getProperty().getHost().getId()))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(host.getEmail()))
                .andExpect(jsonPath("$.fullName").value(host.getFullName()));
    }

    @Test
    void shouldFailGetUserDetailsByIdNoBookingsFound() throws Exception {
        host = new User(null,"janedoe91@gmail.com","password456","Jane Doe",
                null,UserStatus.ACTIVE,null,Role.HOST,null,null);
        host = userRepository.save(host);

        property = new Property(null,"Test title","Test description", "Test City", PropertyType.APARTMENT, 200, 1,"Test address",
                new BigDecimal(100.0),3,true,0.0,host,null,null,null);
        property = propertyRepository.save(property);

        booking = new Booking(null, LocalDate.now(),LocalDate.now(),user,property,BookingStatus.CONFIRMED);
        booking = bookingRepository.save(booking);

        mockMvc.perform(get("/api/user/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("No bookings found"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        PasswordDto passwordDto = new PasswordDto("password123");
        mockMvc.perform(delete("/api/user")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto)))
                .andExpect(status().isNoContent());

        Assertions.assertThat(userRepository.findById(user.getId())).isEmpty();
    }
}
