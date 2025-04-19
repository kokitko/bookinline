package com.bookinline.bookinline.service;

import com.bookinline.bookinline.dto.PropertyRequestDto;
import com.bookinline.bookinline.dto.PropertyResponseDto;
import com.bookinline.bookinline.dto.PropertyResponsePage;

public interface PropertyService {
    PropertyResponseDto createProperty(PropertyRequestDto propertyRequestDto);
    PropertyResponseDto updateProperty(Long id, PropertyRequestDto propertyRequestDto);
    void deleteProperty(Long id);
    PropertyResponseDto getPropertyById(Long id);
    PropertyResponsePage getAvailableProperties(int page, int size);
}
