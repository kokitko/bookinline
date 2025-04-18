package entity;

import jakarta.persistence.*;

import javax.management.relation.Role;
import java.util.List;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role; // HOST, GUEST, ADMIN

    @OneToMany(mappedBy = "host")
    private List<Property> properties;

    @OneToMany(mappedBy = "guest")
    private List<Booking> bookings;
}
