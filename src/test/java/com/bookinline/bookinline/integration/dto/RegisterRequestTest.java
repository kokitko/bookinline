package com.bookinline.bookinline.integration.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RegisterRequestTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFullNameIsBlank() throws Exception {
        String request = """
                    {
                        "fullName": "",
                        "email": "goodemail@example.com",
                        "password": "password123",
                        "role": "GUEST"
                    }
                """;

        String response = mockMvc.perform(post("/api/auth/register")
                    .content(request)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Assertions.assertThat(response).contains("Validation failed: fullName:");
    }

    @Test
    public void testFullNameSize() throws Exception {
        String request = """
                    {
                        "fullName": "12",
                        "email": "goodemail@example.com",
                        "password": "password123",
                        "role": "GUEST"
                    }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value
                        ("Validation failed: fullName: size must be between 3 and 50"));
    }

    @Test
    public void testEmailFormat() throws Exception {
        String request = """
                    {
                        "fullName": "Test Full Name",
                        "email": "badEmail",
                        "password": "password123",
                        "role": "GUEST"
                    }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value
                        ("Validation failed: email: must be a well-formed email address"));
    }

    @Test
    public void testEmailNotBlank() throws Exception {
        String request = """
                    {
                        "fullName": "Test Full Name",
                        "email": "",
                        "password": "password123",
                        "role": "GUEST"
                    }
                """;

        String response = mockMvc.perform(post("/api/auth/register")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Assertions.assertThat(response).contains("Validation failed: email: Email is required");
    }

        @Test
        public void testPasswordNotBlank() throws Exception {
            String request = """
                    {
                        "fullName": "Test Full Name",
                        "email": "goodemail@example.com",
                        "password": "",
                        "role": "GUEST"
                    }
                """;

            String response = mockMvc.perform(post("/api/auth/register")
                            .content(request)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            Assertions.assertThat(response).contains("Validation failed: password:");
        }

    @Test
    public void testPasswordSize() throws Exception {
        String request = """
                    {
                        "fullName": "Test Full Name",
                        "email": "goodemail@example.com",
                        "password": "12345",
                        "role": "GUEST"
                    }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value
                        ("Validation failed: password: Password must be between 6 and 20 characters"));
    }

    @Test
    public void testRoleNotNull() throws Exception{
        String request = """
                    {
                        "fullName": "Test Full Name",
                        "email": "goodemail@example.com",
                        "password": "password123",
                        "role": null
                    }
                """;

        mockMvc.perform(post("/api/auth/register")
                .content(request)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value
                        ("Validation failed: role: Role is required"));

    }
}
