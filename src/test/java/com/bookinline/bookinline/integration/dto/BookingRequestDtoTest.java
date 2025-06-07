package com.bookinline.bookinline.integration.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

    @Test
    public void testNullCheckInDate() throws Exception {
        String requestBody = """
                {
                    "checkInDate": null,
                    "checkOutDate": "15/10/2028"
                }
                """;

        String response = mockMvc.perform(post("/api/bookings/property/1")
                .contentType("application/json")
                .content(requestBody))
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

        String response = mockMvc.perform(post("/api/bookings/property/1")
                .contentType("application/json")
                .content(requestBody))
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

        mockMvc.perform(post("/api/bookings/property/1")
                .contentType("application/json")
                .content(requestBody))
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

        mockMvc.perform(post("/api/bookings/property/1")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed: checkOutDate: Check out date must be in the future"));
    }
}
