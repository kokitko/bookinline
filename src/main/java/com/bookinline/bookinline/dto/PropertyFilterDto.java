package com.bookinline.bookinline.dto;

import com.bookinline.bookinline.entity.enums.PropertyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyFilterDto {
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
    private String sortBy; // price, rating
    private String sortOrder; // ASC, DESC
}
