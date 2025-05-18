package com.bookinline.bookinline.unit.service;

import com.bookinline.bookinline.dto.PropertyRequestDto;
import com.bookinline.bookinline.dto.PropertyResponseDto;
import com.bookinline.bookinline.dto.PropertyResponsePage;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.PropertyType;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.service.impl.PropertyServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PropertyServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PropertyRepository propertyRepository;
    @InjectMocks
    private PropertyServiceImpl propertyService;

    private User user = new User();
    private Property property = new Property();
    private PropertyRequestDto propertyRequestDto = new PropertyRequestDto();

    @BeforeEach
    public void setup() {
        property.setTitle("Luxury Villa");
        property.setDescription("A luxury villa with a sea view.");
        property.setCity("Beach City");
        property.setPropertyType(PropertyType.VILLA);
        property.setFloorArea(200);
        property.setBedrooms(3);
        property.setAddress("456 Ocean Ave");
        property.setPricePerNight(new BigDecimal("500.00"));
        property.setMaxGuests(6);
        property.setAvailable(true);
        property.setHost(user);

        propertyRequestDto.setTitle("Luxury Villa");
        propertyRequestDto.setDescription("A luxury villa with a sea view.");
        propertyRequestDto.setCity("Beach City");
        propertyRequestDto.setPropertyType("VILLA");
        propertyRequestDto.setFloorArea(200);
        propertyRequestDto.setBedrooms(3);
        propertyRequestDto.setAddress("456 Ocean Ave");
        propertyRequestDto.setPricePerNight(new BigDecimal("500.00"));
        propertyRequestDto.setMaxGuests(6);
    }

    @Test
    public void PropertyService_GetPropertyById_ReturnsPropertyResponseDto() {
        when(propertyRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(property));

        PropertyResponseDto propertyResponse = propertyService.getPropertyById(1L);
        Assertions.assertThat(propertyResponse).isNotNull();
        Assertions.assertThat(propertyResponse.getTitle()).isEqualTo(property.getTitle());
    }

    @Test
    public void PropertyService_GetAvailableProperties_ReturnsPropertyResponsePage() {
        Page<Property> propertyPage = Mockito.mock(Page.class);
        Pageable pageable = PageRequest.of(0, 10);
        when(propertyRepository.findByAvailableTrue(pageable)).thenReturn(propertyPage);
        when(propertyPage.getContent()).thenReturn(List.of(property));
        when(propertyPage.getTotalElements()).thenReturn(1L);
        when(propertyPage.getTotalPages()).thenReturn(1);

        PropertyResponsePage responsePage = propertyService.getAvailableProperties(0, 10);

        Assertions.assertThat(responsePage).isNotNull();
        Assertions.assertThat(responsePage.getTotalElements()).isEqualTo(1L);
        Assertions.assertThat(responsePage.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(responsePage.getProperties()).hasSize(1);
        Assertions.assertThat(responsePage.getProperties().get(0).getTitle()).isEqualTo(property.getTitle());
    }
}
