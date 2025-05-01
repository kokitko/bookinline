package com.bookinline.bookinline.security;

import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.entity.enums.UserStatus;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class SecurityIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PropertyRepository propertyRepository;

    @Test
    public void publicEndpointsAreAccessible() throws Exception {
        User guest = new User(null,"johndoe88@gmail.com","password123","John Doe",null, UserStatus.ACTIVE,null, Role.GUEST,null,null);
        guest = userRepository.save(guest);
        Property property = new Property(null,"test","test","test",new BigDecimal(100.0),3,true,0.0,guest,null,null,null);
        property = propertyRepository.save(property);

        mockMvc.perform(get("/api/properties/available")).andExpect(status().isOk());
        mockMvc.perform(get("/api/bookings/property/" + property.getId() + "/dates")).andExpect(status().isOk());
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
