package com.bookinline.bookinline.unit.controller;

import com.bookinline.bookinline.controller.PropertyController;
import com.bookinline.bookinline.dto.PropertyResponseDto;
import com.bookinline.bookinline.dto.PropertyResponsePage;
import com.bookinline.bookinline.security.JwtAuthFilter;
import com.bookinline.bookinline.service.PropertyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(PropertyController.class)
public class PropertyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PropertyService propertyService;
    @MockBean
    private JwtAuthFilter jwtAuthFilter;
    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("Get property by ID - successful scenario")
    void testGetPropertyById() throws Exception {
        PropertyResponseDto response = new PropertyResponseDto(1L, null, null,null,
                null, null, null, null, null);

        Mockito.when(propertyService.getPropertyById(Mockito.anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/properties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()));
    }

    @Test
    @DisplayName("Get available properties page - successful scenario")
    void testGetAvailableProperties() throws Exception {
        PropertyResponsePage responsePage = new PropertyResponsePage(
                0, 10, 1, 2, true, List.of(
                        new PropertyResponseDto(1L, null, null, null, null, null, null, null, null),
                        new PropertyResponseDto(2L, null, null, null, null, null, null, null, null)
                )
        );

        Mockito.when(propertyService.getAvailableProperties(Mockito.anyInt(), Mockito.anyInt())).thenReturn(responsePage);

        mockMvc.perform(get("/api/properties/available")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.properties[0].id").value(responsePage.getProperties().get(0).getId()))
                .andExpect(jsonPath("$.properties[1].id").value(responsePage.getProperties().get(1).getId()));
    }
}
