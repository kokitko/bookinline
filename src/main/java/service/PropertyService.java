package service;

import dto.PropertyRequestDto;
import dto.PropertyResponseDto;
import dto.PropertyResponsePage;

import java.util.List;

public interface PropertyService {
    PropertyResponseDto createProperty(PropertyRequestDto propertyRequestDto);
    PropertyResponseDto updateProperty(Long id, PropertyRequestDto propertyRequestDto);
    void deleteProperty(Long id);
    PropertyResponseDto getPropertyById(Long id);
    PropertyResponsePage getAvailableProperties(int page, int size);
}
