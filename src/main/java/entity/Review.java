package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class Review {
    @Id
    @GeneratedValue
    private Long id;

    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    @ManyToOne
    private User author;

    @ManyToOne Property property;
}
