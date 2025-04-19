package com.bookinline.bookinline.service.impl;

import com.bookinline.bookinline.dto.PropertyRequestDto;
import com.bookinline.bookinline.dto.PropertyResponseDto;
import com.bookinline.bookinline.dto.PropertyResponsePage;
import com.bookinline.bookinline.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.bookinline.bookinline.repositories.PropertyRepository;
import com.bookinline.bookinline.service.PropertyService;

import java.util.List;

@Service
public class PropertyServiceImpl implements PropertyService {
    private final PropertyRepository propertyRepository;

    public PropertyServiceImpl(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Override
    public PropertyResponseDto createProperty(PropertyRequestDto propertyRequestDto) {
        Property property = mapPropertyDtoToEntity(propertyRequestDto);
        Property savedProperty = propertyRepository.save(property);
        return mapPropertyEntityToDto(savedProperty);
    }

    @Override
    public PropertyResponseDto updateProperty(Long id, PropertyRequestDto propertyRequestDto) {
        Property property = mapPropertyDtoToEntity(propertyRequestDto);
        property.setId(id);
        Property updatedProperty = propertyRepository.save(property);
        return mapPropertyEntityToDto(updatedProperty);
    }

    @Override
    public void deleteProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        propertyRepository.delete(property);
    }

    @Override
    public PropertyResponseDto getPropertyById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        return mapPropertyEntityToDto(property);
    }

    @Override
    public PropertyResponsePage getAvailableProperties(int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Property> propertyPage = propertyRepository.findByAvailableTrue(pageable);
        List<PropertyResponseDto> propertyResponseDtos = propertyPage.getContent()
                .stream()
                .map(this::mapPropertyEntityToDto).toList();

        PropertyResponsePage propertyResponsePage = new PropertyResponsePage();
        propertyResponsePage.setPage(propertyPage.getNumber());
        propertyResponsePage.setSize(propertyPage.getSize());
        propertyResponsePage.setTotalElements(propertyPage.getTotalElements());
        propertyResponsePage.setTotalPages(propertyPage.getTotalPages());
        propertyResponsePage.setLast(propertyPage.isLast());
        propertyResponsePage.setProperties(propertyResponseDtos);
        return propertyResponsePage;
    }

    private PropertyResponseDto mapPropertyEntityToDto(Property property) {
        return PropertyResponseDto.builder()
                .id(property.getId())
                .title(property.getTitle())
                .description(property.getDescription())
                .address(property.getAddress())
                .pricePerNight(property.getPricePerNight())
                .maxGuests(property.getMaxGuests())
                .available(property.getAvailable())
                .averageRating(property.getAverageRating())
                .build();
    }

    private Property mapPropertyDtoToEntity(PropertyRequestDto propertyRequestDto) {
        return Property.builder()
                .title(propertyRequestDto.getTitle())
                .description(propertyRequestDto.getDescription())
                .address(propertyRequestDto.getAddress())
                .pricePerNight(propertyRequestDto.getPricePerNight())
                .maxGuests(propertyRequestDto.getMaxGuests())
                .available(propertyRequestDto.getAvailable())
                .build();

    }
}
