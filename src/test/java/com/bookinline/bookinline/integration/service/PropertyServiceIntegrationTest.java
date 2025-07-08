package com.bookinline.bookinline.integration.service;

import com.bookinline.bookinline.dto.PropertyFilterDto;
import com.bookinline.bookinline.dto.PropertyRequestDto;
import com.bookinline.bookinline.dto.PropertyResponseDto;
import com.bookinline.bookinline.dto.PropertyResponsePage;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.PropertyType;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.entity.enums.UserStatus;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.service.PropertyService;
import com.bookinline.bookinline.service.S3Service;
import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
    @MockBean
    private S3Service s3Service;

    private User user = new User();
    private Property property = new Property();
    private PropertyRequestDto propertyRequestDto = new PropertyRequestDto();

    private Property property1 = new Property();
    private Property property2 = new Property();
    private Property property3 = new Property();

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
        property.setPropertyType(PropertyType.VILLA);
        property.setCity("Beach City");
        property.setFloorArea(200);
        property.setBedrooms(3);
        property.setAddress("456 Ocean Ave, Beach City");
        property.setPricePerNight(new BigDecimal("500.00"));
        property.setMaxGuests(6);
        property.setAvailable(true);
        property.setHost(user);

        propertyRequestDto.setTitle("Luxury Villa");
        propertyRequestDto.setDescription("A luxury villa with a sea view.");
        propertyRequestDto.setPropertyType("VILLA");
        propertyRequestDto.setCity("Beach City");
        propertyRequestDto.setFloorArea(200);
        propertyRequestDto.setBedrooms(3);
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

    @Test
    public void PropertyService_GetFilteredPropertiesByDesc_ReturnsFilteredProperties() {
        initializeMoreProperties();

        PropertyFilterDto filter = new PropertyFilterDto();
        filter.setCity("Beach City");
        filter.setMinPrice(new BigDecimal("140.00"));
        filter.setSortBy("pricePerNight");
        filter.setSortOrder("DESC");

        PropertyResponsePage page = propertyService.getFilteredProperties(filter, 0, 10);
        Assertions.assertThat(page).isNotNull();
        Assertions.assertThat(page.getProperties().size()).isEqualTo(3);
        Assertions.assertThat(page.getProperties().getFirst().getPricePerNight()).isEqualTo(new BigDecimal("700.00"));
    }

    @Test
    public void PropertyService_GetFilteredPropertiesByApartment_ReturnsFilteredProperties() {
        initializeMoreProperties();

        PropertyFilterDto filter = new PropertyFilterDto();
        filter.setPropertyType(PropertyType.APARTMENT);
        filter.setSortBy("pricePerNight");
        filter.setSortOrder("DESC");

        PropertyResponsePage page = propertyService.getFilteredProperties(filter, 0, 10);
        Assertions.assertThat(page).isNotNull();
        Assertions.assertThat(page.getProperties().size()).isEqualTo(1);
        Assertions.assertThat(page.getProperties().getFirst().getTitle()).isEqualTo(property1.getTitle());
    }

    private void initializeMoreProperties() {
        property = propertyRepository.save(property);

        property1.setTitle("Cozy Apartment");
        property1.setDescription("A cozy apartment in the city center.");
        property1.setPropertyType(PropertyType.APARTMENT);
        property1.setCity("Beach City");
        property1.setFloorArea(80);
        property1.setBedrooms(2);
        property1.setAddress("123 Main St");
        property1.setPricePerNight(new BigDecimal("150.00"));
        property1.setMaxGuests(4);
        property1.setAvailable(true);
        property1.setHost(user);
        propertyRepository.save(property1);

        property2.setTitle("Studio Apartment");
        property2.setDescription("A studio apartment with a sea view.");
        property2.setPropertyType(PropertyType.STUDIO);
        property2.setCity("Beach City");
        property2.setFloorArea(50);
        property2.setBedrooms(1);
        property2.setAddress("789 Ocean Blvd");
        property2.setPricePerNight(new BigDecimal("100.00"));
        property2.setMaxGuests(2);
        property2.setAvailable(true);
        property2.setHost(user);
        propertyRepository.save(property2);

        property3.setTitle("Farmhouse");
        property3.setDescription("A farmhouse in the countryside.");
        property3.setPropertyType(PropertyType.FARMHOUSE);
        property3.setCity("Beach City");
        property3.setFloorArea(300);
        property3.setBedrooms(5);
        property3.setAddress("101 Country Rd");
        property3.setPricePerNight(new BigDecimal("700.00"));
        property3.setMaxGuests(10);
        property3.setAvailable(true);
        property3.setHost(user);
        propertyRepository.save(property3);
    }
}
