package com.bookinline.bookinline.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Property {
    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String description;
    private String address;
    private BigDecimal pricePerNight;

    private Integer maxGuests;
    private Boolean available = true;

    private Double averageRating = 0.0;

    @ManyToOne
    private User host;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "property")
    private List<Booking> bookings;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;
}
