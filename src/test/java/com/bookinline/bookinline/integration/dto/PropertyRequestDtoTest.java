package com.bookinline.bookinline.integration.dto;

import lombok.With;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PropertyRequestDtoTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "HOST")
    public void testTitleIsBlank() throws Exception{
        MockMultipartFile propertyPart = new MockMultipartFile(
                "property",
                "",
                "application/json",
                """
                    {
                        "title": "",
                        "description": "A beautiful property",
                        "city": "New York",
                        "floorArea": "80",
                        "bedrooms": 1,
                        "propertyType": "APARTMENT",
                        "address": "123 Main St",
                        "pricePerNight": "100.00",
                        "maxGuests": "2"
                    }
                """.getBytes());

        mockMvc.perform(multipart("/api/properties/create")
                        .file(propertyPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid property data"));
    }

    @Test
    @WithMockUser(roles = "HOST")
    public void testDescriptionIsBlank() throws Exception{
        MockMultipartFile propertyPart = new MockMultipartFile(
                "property",
                "",
                "application/json",
                """
                    {
                        "title": "Property Title",
                        "description": "",
                        "city": "New York",
                        "floorArea": "80",
                        "bedrooms": 1,
                        "propertyType": "APARTMENT",
                        "address": "123 Main St",
                        "pricePerNight": 100.00,
                        "maxGuests": 2
                    }
                """.getBytes());

        mockMvc.perform(multipart("/api/properties/create")
                        .file(propertyPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid property data"));
    }

    @Test
    @WithMockUser(roles = "HOST")
    public void testCityIsBlank() throws Exception {
        MockMultipartFile propertyPart = new MockMultipartFile(
                "property",
                "",
                "application/json",
                """
                    {
                        "title": "Property Title",
                        "description": "A beautiful property",
                        "city": "",
                        "floorArea": "80",
                        "bedrooms": 1,
                        "propertyType": "APARTMENT",
                        "address": "123 Main St",
                        "pricePerNight": 100.00,
                        "maxGuests": 2
                    }
                """.getBytes());

        mockMvc.perform(multipart("/api/properties/create")
                        .file(propertyPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid property data"));
    }

    @Test
    @WithMockUser(roles = "HOST")
    public void testFloorAreaIsNull() throws Exception{
        MockMultipartFile propertyPart = new MockMultipartFile(
                "property",
                "",
                "application/json",
                """
                    {
                        "title": "Property Title",
                        "description": "A beautiful property",
                        "city": "New York",
                        "floorArea": null,
                        "bedrooms": 1,
                        "propertyType": "APARTMENT",
                        "address": "123 Main St",
                        "pricePerNight": 100.00,
                        "maxGuests": 2
                    }
                """.getBytes());

        mockMvc.perform(multipart("/api/properties/create")
                        .file(propertyPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid property data"));
    }

    @Test
    @WithMockUser(roles = "HOST")
    public void testFloorAreaIsNegativeNumber() throws Exception{
        MockMultipartFile propertyPart = new MockMultipartFile(
                "property",
                "",
                "application/json",
                """
                    {
                        "title": "Property Title",
                        "description": "A beautiful property",
                        "city": "New York",
                        "floorArea": -1,
                        "bedrooms": 1,
                        "propertyType": "APARTMENT",
                        "address": "123 Main St",
                        "pricePerNight": 100.00,
                        "maxGuests": 2
                    }
                """.getBytes());

        mockMvc.perform(multipart("/api/properties/create")
                        .file(propertyPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid property data"));
    }

    @Test
    @WithMockUser(roles = "HOST")
    public void testBedroomsIsNull() throws Exception{
        MockMultipartFile propertyPart = new MockMultipartFile(
                "property",
                "",
                "application/json",
                """
                    {
                        "title": "Property Title",
                        "description": "A beautiful property",
                        "city": "New York",
                        "floorArea": "80",
                        "bedrooms": null,
                        "propertyType": "APARTMENT",
                        "address": "123 Main St",
                        "pricePerNight": 100.00,
                        "maxGuests": 2
                    }
                """.getBytes());

        mockMvc.perform(multipart("/api/properties/create")
                        .file(propertyPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid property data"));
    }

    @Test
    @WithMockUser(roles = "HOST")
    public void testBedroomsIsNegativeNumber() throws Exception{
        MockMultipartFile propertyPart = new MockMultipartFile(
                "property",
                "",
                "application/json",
                """
                    {
                        "title": "Property Title",
                        "description": "A beautiful property",
                        "city": "New York",
                        "floorArea": "80",
                        "bedrooms": -1,
                        "propertyType": "APARTMENT",
                        "address": "123 Main St",
                        "pricePerNight": 100.00,
                        "maxGuests": 2
                    }
                """.getBytes());

        mockMvc.perform(multipart("/api/properties/create")
                        .file(propertyPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid property data"));
    }

    @Test
    @WithMockUser(roles = "HOST")
    public void testPropertyTypeIsNull() throws Exception{
        MockMultipartFile propertyPart = new MockMultipartFile(
                "property",
                "",
                "application/json",
                """
                    {
                        "title": "Property Title",
                        "description": "A beautiful property",
                        "city": "New York",
                        "floorArea": "80",
                        "bedrooms": 1,
                        "propertyType": null,
                        "address": "123 Main St",
                        "pricePerNight": 100.00,
                        "maxGuests": 2
                    }
                """.getBytes());

        mockMvc.perform(multipart("/api/properties/create")
                        .file(propertyPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid property data"));
    }

    @Test
    @WithMockUser(roles = "HOST")
    public void testAddressIsBlank() throws Exception{
        MockMultipartFile propertyPart = new MockMultipartFile(
                "property",
                "",
                "application/json",
                """
                    {
                        "title": "Property Title",
                        "description": "A beautiful property",
                        "city": "New York",
                        "floorArea": "80",
                        "bedrooms": 1,
                        "propertyType": "APARTMENT",
                        "address": "",
                        "pricePerNight": 100.00,
                        "maxGuests": 2
                    }
                """.getBytes());

        mockMvc.perform(multipart("/api/properties/create")
                        .file(propertyPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid property data"));
    }

    @Test
    @WithMockUser(roles = "HOST")
    public void testPricePerNightIsNull() throws Exception{
        MockMultipartFile propertyPart = new MockMultipartFile(
                "property",
                "",
                "application/json",
                """
                    {
                        "title": "",
                        "description": "A beautiful property",
                        "city": "New York",
                        "floorArea": "80",
                        "bedrooms": 1,
                        "propertyType": "APARTMENT",
                        "address": "123 Main St",
                        "pricePerNight": null,
                        "maxGuests": 2
                    }
                """.getBytes());

        mockMvc.perform(multipart("/api/properties/create")
                        .file(propertyPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid property data"));
    }

    @Test
    @WithMockUser(roles = "HOST")
    public void testPricePerNightIsNegativeNumber() throws Exception{
        MockMultipartFile propertyPart = new MockMultipartFile(
                "property",
                "",
                "application/json",
                """
                    {
                        "title": "Property Title",
                        "description": "A beautiful property",
                        "city": "New York",
                        "floorArea": "80",
                        "bedrooms": 1,
                        "propertyType": "APARTMENT",
                        "address": "123 Main St",
                        "pricePerNight": 0,
                        "maxGuests": 2
                    }
                """.getBytes());

        mockMvc.perform(multipart("/api/properties/create")
                        .file(propertyPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid property data"));
    }

    @Test
    @WithMockUser(roles = "HOST")
    public void testMaxGuestsIsNull() throws Exception{
        MockMultipartFile propertyPart = new MockMultipartFile(
                "property",
                "",
                "application/json",
                """
                    {
                        "title": "Property Title",
                        "description": "A beautiful property",
                        "city": "New York",
                        "floorArea": "80",
                        "bedrooms": 1,
                        "propertyType": "APARTMENT",
                        "address": "123 Main St",
                        "pricePerNight": -1.00,
                        "maxGuests": null
                    }
                """.getBytes());

        mockMvc.perform(multipart("/api/properties/create")
                        .file(propertyPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid property data"));
    }

    @Test
    @WithMockUser(roles = "HOST")
    public void testMaxGuestsIsNegativeNumber() throws Exception{
        MockMultipartFile propertyPart = new MockMultipartFile(
                "property",
                "",
                "application/json",
                """
                    {
                        "title": "",
                        "description": "A beautiful property",
                        "city": "New York",
                        "floorArea": "80",
                        "bedrooms": 1,
                        "propertyType": "APARTMENT",
                        "address": "123 Main St",
                        "pricePerNight": 100.00,
                        "maxGuests": -1.00
                    }
                """.getBytes());

        mockMvc.perform(multipart("/api/properties/create")
                        .file(propertyPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid property data"));
    }
}
