package com.bookinline.bookinline.integration.controller;

import com.bookinline.bookinline.dto.AuthenticationRequest;
import com.bookinline.bookinline.entity.Booking;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.BookingStatus;
import com.bookinline.bookinline.entity.enums.PropertyType;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.entity.enums.UserStatus;
import com.bookinline.bookinline.repository.BookingRepository;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    private BookingRepository bookingRepository;
    @Autowired
    private Flyway flyway;

    User host;
    Property property;
    Property property2;
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
        property.setCity("Cityville");
        property.setFloorArea(150);
        property.setBedrooms(2);
        property.setPropertyType(PropertyType.APARTMENT);
        property.setAddress("123 Main St");
        property.setPricePerNight(new BigDecimal("100.00"));
        property.setMaxGuests(2);
        property.setAvailable(true);
        property.setHost(host);
        property = propertyRepository.save(property);

        property2 = new Property();
        property2.setTitle("Luxury Villa");
        property2.setDescription("A luxury villa with a sea view.");
        property2.setCity("Beach City");
        property2.setFloorArea(300);
        property2.setBedrooms(4);
        property2.setPropertyType(PropertyType.VILLA);
        property2.setAddress("456 Ocean Drive");
        property2.setPricePerNight(new BigDecimal("500.00"));
        property2.setMaxGuests(8);
        property2.setAvailable(true);
        property2.setHost(host);
        property2 = propertyRepository.save(property2);

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
                    "propertyType": "APARTMENT",
                    "city": "test city",
                    "floorArea": "100",
                    "bedrooms": "2",
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
                    "propertyType": "APARTMENT",
                    "city": "test city",
                    "floorArea": "100",
                    "bedrooms": "2",
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
                    "propertyType": "APARTMENT",
                    "city": "Updated city",
                    "floorArea": "200",
                    "bedrooms": "3",
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
                    "propertyType": "APARTMENT",
                    "city": "Updated city",
                    "floorArea": "200",
                    "bedrooms": "3",
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

    @Test
    void shouldReturnFilteredByPriceProperties() throws Exception {
        String filters = """
                {
                "checkIn": "01/01/2020",
                "checkOut": "01/01/2030",
                "title": null,
                "city": null,
                "propertyType": null,
                "minFloorArea": null,
                "maxFloorArea": null,
                "minBedrooms": null,
                "maxBedrooms": null,
                "address": null,
                "minPrice": null,
                "maxPrice": null,
                "minGuests": null,
                "maxGuests": null,
                "minRating": null,
                "maxRating": null,
                "sortBy": "pricePerNight",
                "sortOrder": "DESC"}
                """;
        mockMvc.perform(post("/api/properties/filter")
                    .param("page", "0")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(filters))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.properties[0].title").value(property2.getTitle()))
                .andExpect(jsonPath("$.properties[1].title").value(property.getTitle()));
    }

    @Test
    void shouldReturnFilteredByMaxGuestsAndCityProperties() throws Exception {
        String filters = """
                {
                "checkIn": "01/01/2020",
                "checkOut": "01/01/2030",
                "title": null,
                "city": "Beach City",
                "propertyType": null,
                "minFloorArea": null,
                "maxFloorArea": null,
                "minBedrooms": null,
                "maxBedrooms": null,
                "address": null,
                "minPrice": null,
                "maxPrice": null,
                "minGuests": 3,
                "maxGuests": 8,
                "minRating": null,
                "maxRating": null,
                "sortBy": null,
                "sortOrder": null}
                """;
        mockMvc.perform(post("/api/properties/filter")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filters))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.properties[0].title").value(property2.getTitle()));
    }

    @Test
    void shouldReturnZeroAvailablePropertiesAfterSorting() throws Exception {
        Booking booking = new Booking();
        booking.setCheckInDate(LocalDate.of(2023, 1, 1));
        booking.setCheckOutDate(LocalDate.of(2023, 1, 10));
        booking.setProperty(property2);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setGuest(host);
        booking = bookingRepository.save(booking);
        String filters = """
                {
                "checkIn": "01/01/2020",
                "checkOut": "01/01/2030",
                "title": null,
                "city": "Beach City",
                "propertyType": null,
                "minFloorArea": null,
                "maxFloorArea": null,
                "minBedrooms": null,
                "maxBedrooms": null,
                "address": null,
                "minPrice": null,
                "maxPrice": null,
                "minGuests": 3,
                "maxGuests": 8,
                "minRating": null,
                "maxRating": null,
                "sortBy": null,
                "sortOrder": null}
                """;
        mockMvc.perform(post("/api/properties/filter")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filters))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.properties").isEmpty());
    }
}
