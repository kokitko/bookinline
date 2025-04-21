package com.bookinline.bookinline.service;

import com.bookinline.bookinline.dto.PropertyRequestDto;
import com.bookinline.bookinline.dto.PropertyResponseDto;
import com.bookinline.bookinline.dto.PropertyResponsePage;

public interface PropertyService {
    PropertyResponseDto createProperty(PropertyRequestDto propertyRequestDto, Long userId);
    PropertyResponseDto updateProperty(Long propertyId, PropertyRequestDto propertyRequestDto, Long userId);
    void deleteProperty(Long propertyId, Long userId);
    PropertyResponseDto getPropertyById(Long id);
    PropertyResponsePage getAvailableProperties(int page, int size);
}
