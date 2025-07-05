package com.bookinline.bookinline.service;

import com.bookinline.bookinline.dto.PropertyFilterDto;
import com.bookinline.bookinline.dto.PropertyRequestDto;
import com.bookinline.bookinline.dto.PropertyResponseDto;
import com.bookinline.bookinline.dto.PropertyResponsePage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PropertyService {
    PropertyResponseDto createProperty(PropertyRequestDto propertyRequestDto,
                                       Long userId, List<MultipartFile> images);
    PropertyResponseDto updateProperty(Long propertyId, PropertyRequestDto propertyRequestDto,
                                       Long userId, List<MultipartFile> images);
    void deleteProperty(Long propertyId, Long userId);
    PropertyResponseDto getPropertyById(Long id);
    PropertyResponsePage getAvailableProperties(int page, int size);
    PropertyResponsePage getFilteredProperties(PropertyFilterDto propertyFilterDto, int page, int size);
    PropertyResponsePage getPropertiesByHostId(Long hostId, int page, int size);
}
