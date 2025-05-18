package com.bookinline.bookinline.unit.controller;

import com.bookinline.bookinline.controller.AdminController;
import com.bookinline.bookinline.dto.BookingResponseDto;
import com.bookinline.bookinline.dto.PropertyResponseDto;
import com.bookinline.bookinline.dto.ReviewResponseDto;
import com.bookinline.bookinline.dto.UserResponseDto;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.security.JwtAuthFilter;
import com.bookinline.bookinline.service.AdminService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AdminController.class)
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AdminService adminService;
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
    @DisplayName("Get user details - successful scenario")
    void testGetUserDetails() throws Exception {
        UserResponseDto response = new UserResponseDto("testemail@example.com",null,null,null,null);

        Mockito.when(adminService.getUserById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/admin/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("testemail@example.com"));
    }

    @Test
    @DisplayName("Get property details - successful scenario")
    void testGetPropertyDetails() throws Exception {
        PropertyResponseDto response = new PropertyResponseDto(1L, "Test Property", null,
                null, null, null, null, null, null,null,null,null,null);

        Mockito.when(adminService.getPropertyById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/admin/properties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Property"));
    }

    @Test
    @DisplayName("Get booking details - successful scenario")
    void testGetBookingDetails() throws Exception {
        BookingResponseDto response = new BookingResponseDto(1L, null, null,
                null, null, null);

        Mockito.when(adminService.getBookingById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/admin/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Get review by ID - successful scenario")
    void testGetReviewById() throws Exception {
        ReviewResponseDto response = new ReviewResponseDto(1L, 5, null, null, null);

        Mockito.when(adminService.getReviewById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/admin/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rating").value(5));
    }
}
