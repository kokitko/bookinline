package com.bookinline.bookinline.integration.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AuthenticationRequestTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testInvalidEmailFormat() throws Exception {
        String invalidEmailRequest = """
                    {
                        "email": "invaliduserexample.com",
                        "password": "validPassword123"
                    }
                """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidEmailRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed: email: must be a well-formed email address"));
    }

    @Test
    public void testEmptyEmail() throws Exception {
        String emptyEmailRequest = """
                    {
                        "email": "",
                        "password": "validPassword123"
                    }
                """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(emptyEmailRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed: email: Email is required"));
    }

    @Test
    public void testShortPassword() throws Exception {
        String invalidPasswordFormat = """
                    {
                        "email": "validemail@example.com",
                        "password": "short"
                    }
                """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPasswordFormat))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed: password: size must be between 6 and 20"));
    }

    @Test
    public void testEmptyPassword() throws Exception {
        String invalidPasswordRequest = """
                    {
                        "email": "validemail@example.com",
                        "password": ""
                    }
                """;

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPasswordRequest))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Assertions.assertThat(response)
                .contains("Validation failed: password:");
    }
}
