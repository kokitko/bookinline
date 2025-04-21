package com.bookinline.bookinline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyRequestDto {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Price per night is required")
    @Positive(message = "Price per night must be a positive number")
    private BigDecimal pricePerNight;

    @NotNull(message = "Max guests is required")
    @Positive(message = "Max guests must be a positive number")
    private Integer maxGuests;

    @NotNull(message = "Availability status is required")
    private Boolean available;
}
