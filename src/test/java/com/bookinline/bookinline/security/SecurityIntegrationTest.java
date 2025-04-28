package com.bookinline.bookinline.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class SecurityIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void publicEndpointsAreAccessible() throws Exception {
        mockMvc.perform(get("/api/properties/available")).andExpect(status().isOk());
        mockMvc.perform(get("/api/bookings/property/1/dates")).andExpect(status().isOk());
        mockMvc.perform(get("/api/user/me")).andExpect(status().isForbidden());
        mockMvc.perform(post("/api/auth/login")).andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/admin/properties/1")).andExpect(status().isForbidden());
    }

    @Test
    void adminEndpointsAreProtected() throws Exception {
        mockMvc.perform(get("/api/admin/something"))
                .andExpect(status().isForbidden());
    }
}
