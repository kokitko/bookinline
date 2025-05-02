package com.bookinline.bookinline.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyResponseDto implements Serializable {
    private static final long serialVersionUID = 102L;
    private Long id;
    private String title;
    private String description;
    private String address;
    private BigDecimal pricePerNight;
    private Integer maxGuests;
    private Boolean available;
    private Double averageRating;
    private List<String> imageUrls;
}
