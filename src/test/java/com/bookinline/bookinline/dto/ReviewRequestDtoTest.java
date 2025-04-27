package com.bookinline.bookinline.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReviewRequestDtoTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testRatingLessThanOne() throws Exception {
        String request = """
                    {
                        "rating": 0,
                        "comment": "Great stay!"
                    }
                """;

        mockMvc.perform(post("/api/reviews/property/1")
                        .content(request)
                        .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Validation failed: rating: Rating must be between 1 and 5"));
    }

    @Test
    public void testRatingGreaterThanFive() throws Exception {
        String request = """
                    {
                        "rating": 6,
                        "comment": "Great stay!"
                    }
                """;

        mockMvc.perform(post("/api/reviews/property/1")
                        .content(request)
                        .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Validation failed: rating: Rating must be between 1 and 5"));
    }

    @Test
    public void testCommentIsBlank() throws Exception {
        String request = """
                    {
                        "rating": 4,
                        "comment": ""
                    }
                """;

        mockMvc.perform(post("/api/reviews/property/1")
                        .content(request)
                        .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Validation failed: comment: Comment cannot be blank"));
    }
}
