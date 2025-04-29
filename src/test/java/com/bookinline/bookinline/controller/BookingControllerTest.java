package com.bookinline.bookinline.controller;

import com.bookinline.bookinline.dto.BookingDatesDto;
import com.bookinline.bookinline.dto.BookingResponseDto;
import com.bookinline.bookinline.dto.BookingResponsePage;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.security.JwtAuthFilter;
import com.bookinline.bookinline.service.BookingService;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;
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
    @DisplayName("Get booking by ID - successful scenario")
    void testGetBookingById() throws Exception {
        BookingResponseDto response = new BookingResponseDto(1L, null, null, null, null, null);

        Mockito.when(bookingService.getBookingById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()));
    }

    @Test
    @DisplayName("Get bookings by user ID - successful scenario")
    void testGetBookingsByUserId() throws Exception {
        BookingResponsePage response = new BookingResponsePage(
                1, 10, 1, 2, true, List.of(
                new BookingResponseDto(1L, null, null, null, null, null),
                new BookingResponseDto(2L, null, null, null, null, null)
            )
        );

        Mockito.when(bookingService.getBookingsByUserId(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(response);

        mockMvc.perform(get("/api/bookings/user")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(response.getTotalPages()))
                .andExpect(jsonPath("$.bookings[0].id").value(response.getBookings().get(0).getId()))
                .andExpect(jsonPath("$.bookings[1].id").value(response.getBookings().get(1).getId()));
    }

    @Test
    @DisplayName("Get bookings by property ID - successful scenario")
    void testGetBookingsByPropertyId() throws Exception {
        BookingResponsePage response = new BookingResponsePage(
                1, 10, 1, 2, true, List.of(
                new BookingResponseDto(1L, null, null, null, null, null),
                new BookingResponseDto(2L, null, null, null, null, null)
            )
        );

        Mockito.when(bookingService.getBookingsByPropertyId(Mockito.anyLong(), Mockito.anyLong(),
                Mockito.anyInt(), Mockito.anyInt())).thenReturn(response);

        mockMvc.perform(get("/api/bookings/property/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(response.getTotalPages()))
                .andExpect(jsonPath("$.bookings[0].id").value(response.getBookings().get(0).getId()))
                .andExpect(jsonPath("$.bookings[1].id").value(response.getBookings().get(1).getId()));
    }

    @Test
    @DisplayName("Get booking dates by property ID - successful scenario")
    void testGetBookingDatesByPropertyId() throws Exception {
        List<BookingDatesDto> response = List.of(
                new BookingDatesDto(LocalDate.now(), LocalDate.now().plusDays(5)),
                new BookingDatesDto(LocalDate.now().plusDays(10), LocalDate.now().plusDays(15))
        );

        Mockito.when(bookingService.getBookedDatesByPropertyId(Mockito.anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/bookings/property/1/dates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].startDate").value(response.get(0).getStartDate().toString()))
                .andExpect(jsonPath("$[0].endDate").value(response.get(0).getEndDate().toString()))
                .andExpect(jsonPath("$[1].startDate").value(response.get(1).getStartDate().toString()))
                .andExpect(jsonPath("$[1].endDate").value(response.get(1).getEndDate().toString()));
    }
}
