package com.bookinline.bookinline.mapper;

import com.bookinline.bookinline.dto.PropertyRequestDto;
import com.bookinline.bookinline.dto.PropertyResponseDto;
import com.bookinline.bookinline.dto.PropertyResponsePage;
import com.bookinline.bookinline.entity.Property;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PropertyMapperTest {
    private final PropertyMapper propertyMapper = new PropertyMapper();

    @Test
    void shouldMapToPropertyResponseDto() {
        Property property = new Property(1L, "Test Property", "Test Description", "Test Address",
                BigDecimal.valueOf(100), 2, true, 4.5, null, new ArrayList<>(), null, null);

        PropertyResponseDto responseDto = propertyMapper.mapToPropertyResponseDto(property);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getId()).isEqualTo(property.getId());
        assertThat(responseDto.getTitle()).isEqualTo(property.getTitle());
        assertThat(responseDto.getDescription()).isEqualTo(property.getDescription());
        assertThat(responseDto.getAddress()).isEqualTo(property.getAddress());
        assertThat(responseDto.getPricePerNight()).isEqualTo(property.getPricePerNight());
        assertThat(responseDto.getMaxGuests()).isEqualTo(property.getMaxGuests());
        assertThat(responseDto.getAvailable()).isEqualTo(property.getAvailable());
        assertThat(responseDto.getAverageRating()).isEqualTo(property.getAverageRating());
        assertThat(responseDto.getImageUrls()).isNotNull();
    }

    @Test
    void shouldMapToPropertyEntity() {
        PropertyRequestDto requestDto = new PropertyRequestDto("Test Property", "Test Description",
                "Test Address", BigDecimal.valueOf(100), 2);

        Property property = propertyMapper.mapToPropertyEntity(requestDto);

        assertThat(property).isNotNull();
        assertThat(property.getTitle()).isEqualTo(requestDto.getTitle());
        assertThat(property.getDescription()).isEqualTo(requestDto.getDescription());
        assertThat(property.getAddress()).isEqualTo(requestDto.getAddress());
        assertThat(property.getPricePerNight()).isEqualTo(requestDto.getPricePerNight());
        assertThat(property.getMaxGuests()).isEqualTo(requestDto.getMaxGuests());
    }

    @Test
    void shouldMapToPropertyResponsePage() {
        List<Property> properties = List.of(
                new Property(1L, "Test Property 1", "Test Description 1", "Test Address 1",
                        BigDecimal.valueOf(100), 2, true, 4.5, null, new ArrayList<>(), null, null),
                new Property(2L, "Test Property 2", "Test Description 2", "Test Address 2",
                        BigDecimal.valueOf(200), 4, true, 4.0, null, new ArrayList<>(), null, null)
        );

        Page<Property> page = mock(Page.class);
        when(page.getNumber()).thenReturn(1);
        when(page.getSize()).thenReturn(10);
        when(page.getTotalElements()).thenReturn(1L);
        when(page.getTotalPages()).thenReturn(10);
        when(page.isLast()).thenReturn(true);
        when(page.getContent()).thenReturn(properties);

        PropertyResponsePage propertyResponsePage = propertyMapper.mapToPropertyResponsePage(page);

        assertThat(propertyResponsePage).isNotNull();
        assertThat(propertyResponsePage.getPage()).isEqualTo(1);
        assertThat(propertyResponsePage.getSize()).isEqualTo(10);
        assertThat(propertyResponsePage.getTotalElements()).isEqualTo(1L);
        assertThat(propertyResponsePage.getTotalPages()).isEqualTo(10);
        assertThat(propertyResponsePage.isLast()).isTrue();
        assertThat(propertyResponsePage.getProperties()).isNotNull();
        assertThat(propertyResponsePage.getProperties()).hasSize(2);
        assertThat(propertyResponsePage.getProperties().get(0).getId()).isEqualTo(properties.get(0).getId());
        assertThat(propertyResponsePage.getProperties().get(1).getId()).isEqualTo(properties.get(1).getId());
    }
}
