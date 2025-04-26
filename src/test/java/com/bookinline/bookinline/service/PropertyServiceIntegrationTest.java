package com.bookinline.bookinline.service;

import com.bookinline.bookinline.dto.PropertyRequestDto;
import com.bookinline.bookinline.dto.PropertyResponseDto;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.entity.enums.UserStatus;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PropertyServiceIntegrationTest {
    @Autowired
    private PropertyService propertyService;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Flyway flyway;

    private User user = new User();
    private Property property = new Property();
    private PropertyRequestDto propertyRequestDto = new PropertyRequestDto();

    @BeforeEach
    public void setup() {
        flyway.clean();
        flyway.migrate();

        user.setFullName("Jane Doe");
        user.setEmail("janedoe91@gmail.com");
        user.setPassword("password456");
        user.setPhoneNumber("0987654321");
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(Role.HOST);
        userRepository.save(user);

        property.setTitle("Luxury Villa");
        property.setDescription("A luxury villa with a sea view.");
        property.setAddress("456 Ocean Ave, Beach City");
        property.setPricePerNight(new BigDecimal("500.00"));
        property.setMaxGuests(6);
        property.setAvailable(true);
        property.setHost(user);

        propertyRequestDto.setTitle("Luxury Villa");
        propertyRequestDto.setDescription("A luxury villa with a sea view.");
        propertyRequestDto.setAddress("456 Ocean Ave, Beach City");
        propertyRequestDto.setPricePerNight(new BigDecimal("500.00"));
        propertyRequestDto.setMaxGuests(6);
    }

    @Test
    public void PropertyService_CreateProperty_ReturnsPropertyResponseDto() {
        PropertyResponseDto createdProperty = propertyService.createProperty(
                propertyRequestDto, user.getId(), null);

        Property savedProperty = propertyRepository.findById(createdProperty.getId()).orElse(null);
        Assertions.assertThat(savedProperty).isNotNull();
        Assertions.assertThat(savedProperty.getTitle()).isEqualTo(propertyRequestDto.getTitle());
    }

    @Test
    public void PropertyService_UpdateProperty_ReturnsPropertyResponseDto() {
        Property savedProperty = propertyRepository.save(property);
        propertyRequestDto.setTitle("Luxury Villa");
        propertyRequestDto.setDescription("An updated description for the luxury villa.");
        PropertyResponseDto updatedPropertyDto = propertyService.updateProperty(
                savedProperty.getId(), propertyRequestDto, user.getId(), null);

        Property updatedProperty = propertyRepository.findById(updatedPropertyDto.getId()).orElse(null);
        Assertions.assertThat(savedProperty).isNotNull();
        Assertions.assertThat(savedProperty.getTitle()).isEqualTo(propertyRequestDto.getTitle());
        Assertions.assertThat(savedProperty.getDescription()).isEqualTo(propertyRequestDto.getDescription());
    }

    @Test
    public void PropertyService_DeleteProperty_ReturnsVoid() {
        Property savedProperty = propertyRepository.save(property);
        propertyService.deleteProperty(savedProperty.getId(), user.getId());

        Property deletedProperty = propertyRepository.findById(savedProperty.getId()).orElse(null);
        Assertions.assertThat(deletedProperty).isNull();
    }
}
