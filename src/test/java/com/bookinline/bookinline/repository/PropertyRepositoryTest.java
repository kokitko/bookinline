package com.bookinline.bookinline.repository;

import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.Role;
import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
public class PropertyRepositoryTest {
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Flyway flyway;

    User user = new User();
    Property property1 = new Property();
    Property property2 = new Property();

    @BeforeEach
    public void setup() {
        flyway.clean();
        flyway.migrate();

        user.setFullName("Jane Doe");
        user.setEmail("janedoe91@gmail.com");
        user.setPassword("password456");
        user.setPhoneNumber("0987654321");
        user.setRole(Role.HOST);
        userRepository.save(user);

        property1.setTitle("Cozy Apartment");
        property1.setDescription("A cozy apartment in the city center.");
        property1.setAddress("123 Main St, Cityville");
        property1.setPricePerNight(new BigDecimal("100.00"));
        property1.setMaxGuests(2);
        property1.setAvailable(true);
        property1.setHost(user);

        property2.setTitle("Luxury Villa");
        property2.setDescription("A luxury villa with a sea view.");
        property2.setAddress("456 Ocean Ave, Beach City");
        property2.setPricePerNight(new BigDecimal("500.00"));
        property2.setMaxGuests(6);
        property2.setAvailable(true);
        property2.setHost(user);
    }

    @Test
    public void PropertyRepository_FindAllAvailableTrue_ReturnsPage() {
        propertyRepository.saveAll(List.of(property1, property2));
        Pageable pageable = Pageable.ofSize(10);
        Page<Property> properties = propertyRepository.findByAvailableTrue(pageable);

        Assertions.assertThat(properties).isNotNull();
        Assertions.assertThat(properties.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void PropertyRepository_FindById_ReturnsProperty() {
        propertyRepository.saveAll(List.of(property1, property2));
        Property foundProperty = propertyRepository.findById(property1.getId()).orElse(null);

        Assertions.assertThat(foundProperty).isNotNull();
        Assertions.assertThat(foundProperty.getId()).isEqualTo(property1.getId());
    }
}
