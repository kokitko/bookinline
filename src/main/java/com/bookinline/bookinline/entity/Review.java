package com.bookinline.bookinline.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private int rating;
    @Column(nullable = false)
    private String comment;
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    private User author;

    @ManyToOne
    private Property property;
}
