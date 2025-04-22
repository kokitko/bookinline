package com.bookinline.bookinline.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    @Id
    @GeneratedValue
    private Long id;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;
}
