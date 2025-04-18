package entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
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
    private List<Image> images;

    @OneToMany(mappedBy = "property")
    private List<Booking> bookings;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;
}
