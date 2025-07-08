package com.bookinline.bookinline.integration.dto;

import com.bookinline.bookinline.dto.AuthenticationRequest;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.entity.enums.UserStatus;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class BookingRequestDtoTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Flyway flyway;
    @Autowired
    private UserRepository userRepository;
    @MockBean
    private S3Service s3Service;

    User guest;
    String token;

    @BeforeEach
    public void setup() throws Exception {
        flyway.clean();
        flyway.migrate();
        userRepository.deleteAll();

        guest = new User();
        guest.setFullName("John Doe");
        guest.setEmail("johndoe88@gmail.com");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        guest.setPassword(passwordEncoder.encode("password123"));
        guest.setRole(Role.GUEST);
        guest.setStatus(UserStatus.ACTIVE);
        guest.setPhoneNumber("1234567890");
        guest = userRepository.save(guest);

        AuthenticationRequest request = new AuthenticationRequest("johndoe88@gmail.com", "password123");
        token = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
        token = token.substring(16, token.length() - 2);
    }

    @Test
    public void testNullCheckInDate() throws Exception {
        String requestBody = """
                {
                    "checkInDate": null,
                    "checkOutDate": "15/10/2028"
                }
                """;

        String response = mockMvc.perform(post("/api/bookings/property/1/book")
                        .contentType("application/json")
                        .content(requestBody)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Assertions.assertThat(response).contains("Validation failed: checkInDate:");
    }

    @Test
    public void testNullCheckOutDate() throws Exception {
        String requestBody = """
                {
                    "checkInDate": "10/10/2028",
                    "checkOutDate": null
                }
                """;

        String response = mockMvc.perform(post("/api/bookings/property/1/book")
                        .contentType("application/json")
                        .content(requestBody)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Assertions.assertThat(response).contains("Validation failed: checkOutDate:");
    }

    @Test
    public void testCheckInDateInPast() throws Exception {
        String requestBody = """
                {
                    "checkInDate": "10/10/2020",
                    "checkOutDate": "10/10/2028"
                }
                """;

        mockMvc.perform(post("/api/bookings/property/1/book")
                        .contentType("application/json")
                        .content(requestBody)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed: checkInDate: Check in date must be today or in the future"));
    }

    @Test
    public void testCheckOutDateInPast() throws Exception {
        String requestBody = """
                {
                    "checkInDate": "10/10/2028",
                    "checkOutDate": "10/10/2020"
                }
                """;

        mockMvc.perform(post("/api/bookings/property/1/book")
                        .contentType("application/json")
                        .content(requestBody)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed: checkOutDate: Check out date must be in the future"));
    }
}
