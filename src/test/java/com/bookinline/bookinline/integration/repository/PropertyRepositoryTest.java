package com.bookinline.bookinline.integration.repository;

import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.PropertyType;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.service.S3Service;
import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    @MockBean
    private S3Service s3Service;

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
        property1.setCity("Cityville");
        property1.setFloorArea(80);
        property1.setBedrooms(1);
        property1.setPropertyType(PropertyType.APARTMENT);
        property1.setAddress("123 Main St");
        property1.setPricePerNight(new BigDecimal("100.00"));
        property1.setMaxGuests(2);
        property1.setAvailable(true);
        property1.setHost(user);

        property2.setTitle("Luxury Villa");
        property2.setDescription("A luxury villa with a sea view.");
        property2.setCity("Beach City");
        property2.setFloorArea(200);
        property2.setBedrooms(3);
        property2.setPropertyType(PropertyType.VILLA);
        property2.setAddress("456 Ocean Ave");
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

    @Test
    public void PropertyRepository_FindAll_ReturnsAllFilteredAndPaginatedPropertiesPage() {
        propertyRepository.saveAll(List.of(property1, property2));
        Pageable pageable = Pageable.ofSize(10);
        Specification<Property> specification = (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("city"), "Cityville")
            );
        };

        Page<Property> propertiesPage = propertyRepository.findAll(specification, pageable);

        Assertions.assertThat(propertiesPage).isNotNull();
        Assertions.assertThat(propertiesPage.getTotalElements()).isEqualTo(1);
    }
}
