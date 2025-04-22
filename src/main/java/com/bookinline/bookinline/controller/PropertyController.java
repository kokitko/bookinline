package com.bookinline.bookinline.controller;

import com.bookinline.bookinline.dto.PropertyRequestDto;
import com.bookinline.bookinline.dto.PropertyResponseDto;
import com.bookinline.bookinline.dto.PropertyResponsePage;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.exception.UnauthorizedActionException;
import com.bookinline.bookinline.service.PropertyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {
    private final PropertyService propertyService;
    @Autowired
    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PreAuthorize("hasRole('ROLE_HOST')")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PropertyResponseDto> createProperty(@RequestPart("property") String propertyJson,
                                                              @RequestPart(value = "images", required = false)
                                                              List<MultipartFile> images) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        PropertyRequestDto requestDto  = mapper.readValue(propertyJson, PropertyRequestDto.class);
        Long userId = getAuthenticatedUserId();
        PropertyResponseDto createdProperty = propertyService.createProperty(requestDto, userId, images);
        return ResponseEntity.ok(createdProperty);
    }

    @PreAuthorize("hasRole('ROLE_HOST')")
    @PutMapping("/update/{propertyId}")
    public ResponseEntity<PropertyResponseDto> updateProperty(@PathVariable Long propertyId,
                                                              @RequestBody PropertyRequestDto propertyRequestDto) {
        Long userId = getAuthenticatedUserId();
        PropertyResponseDto updatedProperty = propertyService.updateProperty(propertyId, propertyRequestDto, userId);
        return ResponseEntity.ok(updatedProperty);
    }

    @PreAuthorize("hasRole('ROLE_HOST')")
    @DeleteMapping("/delete/{propertyId}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long propertyId) {
        Long userId = getAuthenticatedUserId();
        propertyService.deleteProperty(propertyId, userId);
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

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((User) principal).getId();
            }
        }
        throw new UnauthorizedActionException("Authentication object is null");
    }
}
