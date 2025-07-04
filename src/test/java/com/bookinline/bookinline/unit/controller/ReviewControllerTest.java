package com.bookinline.bookinline.unit.controller;

import com.bookinline.bookinline.controller.ReviewController;
import com.bookinline.bookinline.dto.ReviewResponseDto;
import com.bookinline.bookinline.dto.ReviewResponsePage;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.security.JwtAuthFilter;
import com.bookinline.bookinline.service.ReviewService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ReviewService reviewService;
    @MockBean
    private JwtAuthFilter jwtAuthFilter;
    @MockBean
    private UserDetailsService userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Get review by ID - successful scenario")
    void testGetReviewById() throws Exception {
        ReviewResponseDto response = new ReviewResponseDto(1L, 5, null, null, null, null);

        Mockito.when(reviewService.getReviewById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Get reviews by property ID - successful scenario")
    void testGetReviewsByPropertyId() throws Exception {
        ReviewResponsePage response = new ReviewResponsePage(0,2,1,2,true,
                List.of(
                        new ReviewResponseDto(1L, 5, null, null, null, null),
                        new ReviewResponseDto(2L, 4, null, null, null, null)
                )
        );

        Mockito.when(reviewService.getReviewsByPropertyId(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(response);

        mockMvc.perform(get("/api/reviews/property/1")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviews[0].id").value(1))
                .andExpect(jsonPath("$.reviews[1].id").value(2));
    }

    @Test
    @DisplayName("Get reviews by user ID - successful scenario")
    void testGetReviewsByUserId() throws Exception {
        ReviewResponsePage response = new ReviewResponsePage(0,2,1,2,true,
                List.of(
                        new ReviewResponseDto(1L, 5, null, null, null, null),
                        new ReviewResponseDto(2L, 4, null, null, null, null)
                )
        );

        Mockito.when(reviewService.getReviewsByUserId(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(response);

        mockMvc.perform(get("/api/reviews/user/1")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviews[0].id").value(1))
                .andExpect(jsonPath("$.reviews[1].id").value(2));
    }
}
