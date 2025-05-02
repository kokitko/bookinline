package com.bookinline.bookinline.controller;

import com.bookinline.bookinline.dto.AuthenticationRequest;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.entity.enums.UserStatus;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PropertyControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private Flyway flyway;

    User host;
    Property property;
    String token;

    @BeforeEach
    public void setup() throws Exception {
        flyway.clean();
        flyway.migrate();
        userRepository.deleteAll();

        host = new User();
        host.setFullName("Jane Doe");
        host.setEmail("janedoe91@gmail.com");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        host.setPassword(passwordEncoder.encode("password456"));
        host.setRole(Role.HOST);
        host.setStatus(UserStatus.ACTIVE);
        host.setPhoneNumber("0987654321");
        host = userRepository.save(host);

        property = new Property();
        property.setTitle("Cozy Apartment");
        property.setDescription("A cozy apartment in the city center.");
        property.setAddress("123 Main St, Cityville");
        property.setPricePerNight(new BigDecimal("100.00"));
        property.setMaxGuests(2);
        property.setAvailable(true);
        property.setHost(host);
        property = propertyRepository.save(property);

        AuthenticationRequest request = new AuthenticationRequest("janedoe91@gmail.com", "password456");
        token = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
        token = token.substring(10, token.length() - 2);
    }

    @Test
    void shouldCreatePropertySuccessfully() throws Exception {
        String propertyInfo = """
                    {"title": "test property",
                    "description": "test description",
                    "address": "test address",
                    "pricePerNight": "100.0",
                    "maxGuests": "3"}
                """;

        MockMultipartFile propertyFile = new MockMultipartFile(
                "property",
                "",
                "application/json",
                propertyInfo.getBytes()
        );


        MockMultipartFile file1 = new MockMultipartFile(
                "images",
                "file1.jpg",
                "text/plain",
                "File content 1".getBytes()
        );

        MockMultipartFile file2 = new MockMultipartFile(
                "images",
                "file2.jpg",
                "text/plain",
                "File content 2".getBytes()
        );

        mockMvc.perform(multipart("/api/properties/create")
                        .file(propertyFile)
                        .file(file1)
                        .file(file2)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("test property"));
    }

    @Test
    void shouldFailCreatingProperty() throws Exception {
        String propertyInfo = """
                    {"title: "",
                    "description": "test description",
                    "address": "test address",
                    "pricePerNight": "100.0",
                    "maxGuests": "3"}
                """;

        MockMultipartFile propertyFile = new MockMultipartFile(
                "property",
                "",
                "application/json",
                propertyInfo.getBytes()
        );


        MockMultipartFile file1 = new MockMultipartFile(
                "images",
                "file1.jpg",
                "text/plain",
                "File content 1".getBytes()
        );

        MockMultipartFile file2 = new MockMultipartFile(
                "images",
                "file2.jpg",
                "text/plain",
                "File content 2".getBytes()
        );

        mockMvc.perform(multipart("/api/properties/create")
                .file(propertyFile)
                .file(file1)
                .file(file2)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        mockMvc.perform(multipart("/api/properties/create")
                        .file(propertyFile)
                        .file(file1)
                        .file(file2)
                        .header("Authorization", "Bearer " + token)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid property data"));
    }

    @Test
    void shouldUpdateExistingProperty() throws Exception {
        String propertyInfo = """
                    {"title": "Updated Property",
                    "description": "Updated description",
                    "address": "Updated address",
                    "pricePerNight": "150.0",
                    "maxGuests": "4"}
                """;

        MockMultipartFile propertyFile = new MockMultipartFile(
                "property",
                "",
                "application/json",
                propertyInfo.getBytes()
        );

        mockMvc.perform(multipart("/api/properties/update/" + property.getId())
                        .file(propertyFile)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Property"));
    }

    @Test
    void shouldFailToUpdateExistingProperty() throws Exception {
        String propertyInfo = """
                    {"title: "",
                    "description": "Updated description",
                    "address": "Updated address",
                    "pricePerNight": "150.0",
                    "maxGuests": "4"}
                """;

        MockMultipartFile propertyFile = new MockMultipartFile(
                "property",
                "",
                "application/json",
                propertyInfo.getBytes()
        );

        mockMvc.perform(multipart("/api/properties/update/" + property.getId())
                        .file(propertyFile)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid property data"));
    }

    @Test
    void shouldDeletePropertySuccessfully() throws Exception {
        mockMvc.perform(delete("/api/properties/delete/" + property.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldFailToDeleteProperty() throws Exception {
        mockMvc.perform(delete("/api/properties/delete/99999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Property not found"));
    }

    @Test
    void shouldReturnCertainProperty() throws Exception {
        mockMvc.perform(get("/api/properties/" + property.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(property.getTitle()))
                .andExpect(jsonPath("$.description").value(property.getDescription()))
                .andExpect(jsonPath("$.address").value(property.getAddress()));
    }

    @Test
    void shouldFailToReturnCertainProperty() throws Exception {
        mockMvc.perform(get("/api/properties/99999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Property not found"));
    }

    @Test
    void shouldReturnPaginatedProperties() throws Exception {
        mockMvc.perform(get("/api/properties/available")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.properties[0].title").value(property.getTitle()))
                .andExpect(jsonPath("$.properties[0].description").value(property.getDescription()))
                .andExpect(jsonPath("$.properties[0].address").value(property.getAddress()));
    }
}
