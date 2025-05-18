package com.bookinline.bookinline.dto;

import com.bookinline.bookinline.entity.enums.PropertyType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyFilterDto {
    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate checkIn;
    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate checkOut;
    private String title;
    private String city;
    private PropertyType propertyType;
    private Integer minFloorArea;
    private Integer maxFloorArea;
    private Integer minBedrooms;
    private Integer maxBedrooms;
    private String address;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minGuests;
    private Integer maxGuests;
    private Double minRating;
    private Double maxRating;
    private String sortBy;
    private String sortOrder; // ASC, DESC
}
