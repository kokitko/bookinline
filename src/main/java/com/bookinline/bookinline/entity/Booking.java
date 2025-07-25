package com.bookinline.bookinline.entity;

import com.bookinline.bookinline.entity.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private LocalDate checkInDate;
    @Column(nullable = false)
    private LocalDate checkOutDate;

    @ManyToOne
    private User guest;

    @ManyToOne
    private Property property;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
