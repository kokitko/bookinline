package com.bookinline.bookinline.mapper;

import com.bookinline.bookinline.dto.*;
import com.bookinline.bookinline.entity.Image;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.enums.PropertyType;
import org.springframework.data.domain.Page;

import java.util.List;

public class PropertyMapper {
    public static PropertyResponseDto mapToPropertyResponseDto(Property property) {
        PropertyResponseDto response = PropertyResponseDto.builder()
                .id(property.getId())
                .title(property.getTitle())
                .description(property.getDescription())
                .city(property.getCity())
                .propertyType(String.valueOf(property.getPropertyType()))
                .floorArea(property.getFloorArea())
                .bedrooms(property.getBedrooms())
                .address(property.getAddress())
                .pricePerNight(property.getPricePerNight())
                .maxGuests(property.getMaxGuests())
                .available(property.getAvailable())
                .averageRating(property.getAverageRating())
                .build();

        List<String> urls = property.getImages().stream()
                .map(Image::getImageUrl)
                .toList();
        response.setImageUrls(urls);
        return response;
    }

    public static Property mapToPropertyEntity(PropertyRequestDto propertyRequestDto) {
        return Property.builder()
                .title(propertyRequestDto.getTitle())
                .description(propertyRequestDto.getDescription())
                .city(propertyRequestDto.getCity())
                .propertyType(PropertyType.valueOf(propertyRequestDto.getPropertyType()))
                .floorArea(propertyRequestDto.getFloorArea())
                .bedrooms(propertyRequestDto.getBedrooms())
                .address(propertyRequestDto.getAddress())
                .pricePerNight(propertyRequestDto.getPricePerNight())
                .maxGuests(propertyRequestDto.getMaxGuests())
                .build();
    }

    public static PropertyResponsePage mapToPropertyResponsePage(Page<Property> propertyPage) {
        PropertyResponsePage propertyResponsePage = new PropertyResponsePage();
        propertyResponsePage.setPage(propertyPage.getNumber());
        propertyResponsePage.setSize(propertyPage.getSize());
        propertyResponsePage.setTotalElements(propertyPage.getTotalElements());
        propertyResponsePage.setTotalPages(propertyPage.getTotalPages());
        propertyResponsePage.setLast(propertyPage.isLast());

        List<PropertyResponseDto> propertyResponseDtos = propertyPage.getContent().stream()
                .map(PropertyMapper::mapToPropertyResponseDto)
                .toList();

        propertyResponsePage.setProperties(propertyResponseDtos);
        return propertyResponsePage;
    }
}
