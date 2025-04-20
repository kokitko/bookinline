package com.bookinline.bookinline.controller;

import com.bookinline.bookinline.dto.PropertyRequestDto;
import com.bookinline.bookinline.dto.PropertyResponseDto;
import com.bookinline.bookinline.dto.PropertyResponsePage;
import com.bookinline.bookinline.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {
    private final PropertyService propertyService;
    @Autowired
    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping("/create")
    public ResponseEntity<PropertyResponseDto> createProperty(@RequestBody PropertyRequestDto propertyRequestDto) {
        PropertyResponseDto createdProperty = propertyService.createProperty(propertyRequestDto);
        return ResponseEntity.ok(createdProperty);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PropertyResponseDto> updateProperty(@PathVariable Long id,
                                                              @RequestBody PropertyRequestDto propertyRequestDto) {
        PropertyResponseDto updatedProperty = propertyService.updateProperty(id, propertyRequestDto);
        return ResponseEntity.ok(updatedProperty);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponseDto> getPropertyById(@PathVariable Long id) {
        PropertyResponseDto property = propertyService.getPropertyById(id);
        return ResponseEntity.ok(property);
    }

    @GetMapping("/available")
    public ResponseEntity<PropertyResponsePage> getAvailableProperties(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        PropertyResponsePage availableProperties = propertyService.getAvailableProperties(page, size);
        return ResponseEntity.ok(availableProperties);
    }
}
