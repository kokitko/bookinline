package com.bookinline.bookinline.service.impl;

import com.bookinline.bookinline.dto.PropertyRequestDto;
import com.bookinline.bookinline.dto.PropertyResponseDto;
import com.bookinline.bookinline.dto.PropertyResponsePage;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.Role;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.bookinline.bookinline.repositories.PropertyRepository;
import com.bookinline.bookinline.service.PropertyService;

import java.util.List;

@Service
public class PropertyServiceImpl implements PropertyService {
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public PropertyServiceImpl(PropertyRepository propertyRepository, UserRepository userRepository) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PropertyResponseDto createProperty(PropertyRequestDto propertyRequestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() != Role.HOST && user.getRole() != Role.ADMIN) {
            throw new RuntimeException("User does not have permission to create a property");
        }
        Property property = mapPropertyDtoToEntity(propertyRequestDto);
        property.setHost(user);
        Property savedProperty = propertyRepository.save(property);
        return mapPropertyEntityToDto(savedProperty);
    }

    @Override
    public PropertyResponseDto updateProperty(Long propertyId, PropertyRequestDto propertyRequestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        if (!property.getHost().equals(user) || user.getRole() != Role.ADMIN) {
            throw new RuntimeException("User does not have permission to update this property");
        }
        property.setTitle(propertyRequestDto.getTitle());
        property.setDescription(propertyRequestDto.getDescription());
        property.setAddress(propertyRequestDto.getAddress());
        property.setPricePerNight(propertyRequestDto.getPricePerNight());
        property.setMaxGuests(propertyRequestDto.getMaxGuests());
        property.setAvailable(propertyRequestDto.getAvailable());
        Property updatedProperty = propertyRepository.save(property);

        return mapPropertyEntityToDto(updatedProperty);
    }

    @Override
    public void deleteProperty(Long propertyId, Long userId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!property.getHost().equals(user) || user.getRole() != Role.ADMIN) {
            throw new RuntimeException("User does not have permission to delete this property");
        }
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
