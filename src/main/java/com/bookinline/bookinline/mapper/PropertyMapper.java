package com.bookinline.bookinline.mapper;

import com.bookinline.bookinline.dto.*;
import com.bookinline.bookinline.entity.Image;
import com.bookinline.bookinline.entity.Property;
import org.springframework.data.domain.Page;

import java.util.List;

public class PropertyMapper {
    public static PropertyResponseDto mapToPropertyResponseDto(Property property) {
        PropertyResponseDto response = PropertyResponseDto.builder()
                .id(property.getId())
                .title(property.getTitle())
                .description(property.getDescription())
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
                .address(propertyRequestDto.getAddress())
                .pricePerNight(propertyRequestDto.getPricePerNight())
                .maxGuests(propertyRequestDto.getMaxGuests())
                .available(propertyRequestDto.getAvailable())
                .build();
    }

    public static PropertyResponsePage mapToPropertyResponsePage(Page<Property> propertyPage,
                                                                List<PropertyResponseDto> propertyResponseDtos) {
        PropertyResponsePage propertyResponsePage = new PropertyResponsePage();
        propertyResponsePage.setPage(propertyPage.getNumber());
        propertyResponsePage.setSize(propertyPage.getSize());
        propertyResponsePage.setTotalElements(propertyPage.getTotalElements());
        propertyResponsePage.setTotalPages(propertyPage.getTotalPages());
        propertyResponsePage.setLast(propertyPage.isLast());
        propertyResponsePage.setProperties(propertyResponseDtos);
        return propertyResponsePage;
    }
}
