package com.bookinline.bookinline.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JwtAuthFilterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAllowRequestWithoutAuthorizationHeader() throws Exception {
        mockMvc.perform(get("/api/properties/available"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectRequestIfInvalidJwt() throws Exception {
        mockMvc.perform(get("/api/admin/something")
                        .header("Authorization", "Bearer invalidtoken"))
                .andExpect(status().isUnauthorized());
    }
}
