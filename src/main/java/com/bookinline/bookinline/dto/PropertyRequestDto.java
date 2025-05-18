package com.bookinline.bookinline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyRequestDto {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Property type is required")
    private String propertyType;

    @NotNull(message = "Floor area is required")
    @Positive(message = "Floor area must be a positive number")
    private Integer floorArea;

    @NotNull(message = "Number of bedrooms is required")
    @Positive(message = "Number of bedrooms must be a positive number")
    private Integer bedrooms;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Price per night is required")
    @Positive(message = "Price per night must be a positive number")
    private BigDecimal pricePerNight;

    @NotNull(message = "Max guests is required")
    @Positive(message = "Max guests must be a positive number")
    private Integer maxGuests;
}
