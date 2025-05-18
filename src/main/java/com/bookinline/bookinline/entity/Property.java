package com.bookinline.bookinline.entity;

import com.bookinline.bookinline.entity.enums.PropertyType;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String city;
    @Enumerated(EnumType.STRING)
    private PropertyType propertyType;
    @Column(nullable = false)
    private Integer floorArea;
    @Column(nullable = false)
    private Integer bedrooms;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private BigDecimal pricePerNight;
    @Column(nullable = false)
    private Integer maxGuests;
    @Column(nullable = false)
    private Boolean available = true;
    @Column(nullable = false)
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
