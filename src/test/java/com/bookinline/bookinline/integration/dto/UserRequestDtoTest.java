package com.bookinline.bookinline.integration.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserRequestDtoTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    public void testEmailFormat() throws Exception {
        String request = """
                    {
                        "email": "invalidemail",
                        "password": "password123",
                        "fullName": "Test Full Name",
                        "phoneNumber": "1234567890"
                    }
                """;

        String response = mockMvc.perform(put("/api/user/phone")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Assertions.assertThat(response).contains("Validation failed: email:");
    }

    @Test
    @WithMockUser
    public void testEmailIsBlank() throws Exception {
        String request = """
                    {
                        "email": "",
                        "password": "password123",
                        "fullName": "Test Full Name",
                        "phoneNumber": "1234567890"
                    }
                """;

        mockMvc.perform(put("/api/user/phone")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed: email: Email is required"));
    }

    @Test
    @WithMockUser
    public void testPasswordIsBlank() throws Exception {
        String request = """
                    {
                        "email": "goodemail@example.com",
                        "password": "",
                        "fullName": "Test Full Name",
                        "phoneNumber": "1234567890"
                    }
                """;

        String response = mockMvc.perform(put("/api/user/phone")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Assertions.assertThat(response).contains("Validation failed: password:");
    }

    @Test
    @WithMockUser
    public void testPasswordSize() throws Exception {
        String request = """
                    {
                        "email": "goodemail@example.com",
                        "password": "12345",
                        "fullName": "Test Full Name",
                        "phoneNumber": "1234567890"
                    }
                """;

        mockMvc.perform(put("/api/user/phone")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed: password: Password must be between 6 and 20 characters"));
    }

    @Test
    @WithMockUser
    public void testFullNameIsBlank() throws Exception {
        String request = """
                    {
                        "email": "goodemail@example.com",
                        "password": "password123",
                        "fullName": "",
                        "phoneNumber": "1234567890"
                    }
                """;

        String response = mockMvc.perform(put("/api/user/phone")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Assertions.assertThat(response).contains("Validation failed: fullName:");
    }

    @Test
    @WithMockUser
    public void testFullNameSize() throws Exception {
        String request = """
                    {
                        "email": "goodemail@example.com",
                        "password": "password123",
                        "fullName": "12",
                        "phoneNumber": "1234567890"
                    }
                """;

        mockMvc.perform(put("/api/user/phone")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed: fullName: Full name must be between 2 and 50 characters"));
    }

    @Test
    @WithMockUser
    public void testPhoneNumberFormat() throws Exception {
        String request = """
                    {
                        "email": "goodemail@example.com",
                        "password": "password123",
                        "fullName": "Good Full Name",
                        "phoneNumber": "1.2.3.4.5.6.7.8.9.0"
                    }
                """;

        mockMvc.perform(put("/api/user/phone")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed: phoneNumber: Invalid phone number format"));
    }
}
