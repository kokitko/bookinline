package com.bookinline.bookinline.controller;

import com.bookinline.bookinline.dto.PropertyRequestDto;
import com.bookinline.bookinline.dto.PropertyResponseDto;
import com.bookinline.bookinline.dto.PropertyResponsePage;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.exception.ErrorObject;
import com.bookinline.bookinline.exception.FailedRequestParsingException;
import com.bookinline.bookinline.exception.InvalidPropertyDataException;
import com.bookinline.bookinline.exception.UnauthorizedActionException;
import com.bookinline.bookinline.service.PropertyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
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
import java.util.Set;

@RestController
@RequestMapping("/api/properties")
@Tag(name = "Property", description = "Endpoints for managing properties")
public class PropertyController {
    private Validator validator;
    private final PropertyService propertyService;
    @Autowired
    public PropertyController(PropertyService propertyService, Validator validator) {
        this.propertyService = propertyService;
        this.validator = validator;
    }

    @PreAuthorize("hasRole('ROLE_HOST')")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new property",
            description = """
                    Detailed description of the create property endpoint:
                    - **Endpoint**: `/api/properties/create`
                    - **Method**: `POST`
                    - **Request Body**: Multipart form data containing the property details and optional images.
                    
                    1. The user must be authenticated and have the `ROLE_HOST` role to access this endpoint.
                    2. The request body must contain a JSON object representing the property details.
                    3. The user can optionally include images in the request.
                    4. The server will validate the property data and images.
                    5. The server retrieves the authenticated user's ID from the security context.
                    6. The server uploads the images to the server and associates them with the property.
                    7. The server creates a new property in the database with the provided details and images.
                    8. The server returns a response with the created property details.
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Property created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid property data",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
                    @ApiResponse(responseCode = "403", description = "User does not have permission to create a property",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
            }
    )
    public ResponseEntity<PropertyResponseDto> createProperty(@RequestPart("property") String propertyJson,
                                                              @RequestPart(value = "images", required = false)
                                                              List<MultipartFile> images) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        PropertyRequestDto requestDto;
        try {
            requestDto = mapper.readValue(propertyJson, PropertyRequestDto.class);
            Set<ConstraintViolation<PropertyRequestDto>> violations = validator.validate(requestDto);
            if (!violations.isEmpty()) {
                throw new FailedRequestParsingException("Invalid property data");
            }
        } catch (IOException e) {
            throw new InvalidPropertyDataException("Invalid property data");
        }
        Long userId = getAuthenticatedUserId();
        PropertyResponseDto createdProperty = propertyService.createProperty(requestDto, userId, images);
        return new ResponseEntity<>(createdProperty, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_HOST')")
    @PostMapping("/update/{propertyId}")
    @Operation(summary = "Update an existing property",
            description = """
                    Detailed description of the update property endpoint:
                    - **Endpoint**: `/api/properties/update/{propertyId}`
                    - **Method**: `POST`
                    - **Request Body**: Multipart form data containing the property details and optional images.
                    
                    1. The user must be authenticated and have the `ROLE_HOST` role to access this endpoint.
                    2. The request body must contain a JSON object representing the property details.
                    3. The user can optionally include images in the request.
                    4. The server will validate the property data and images.
                    5. The server retrieves the authenticated user's ID from the security context.
                    6. The server uploads the images to the server and associates them with the property.
                    7. The server updates the existing property in the database with the provided details and images.
                    8. The server returns a response with the updated property details.
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Property updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid property data",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
                    @ApiResponse(responseCode = "403", description = "User does not have permission to update a property",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
                    @ApiResponse(responseCode = "404", description = "User/Property not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
            }
    )
    public ResponseEntity<PropertyResponseDto> updateProperty(@PathVariable Long propertyId,
                                                              @RequestPart("property") String propertyJson,
                                                              @RequestPart(value = "images", required = false)
                                                              List<MultipartFile> images) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        PropertyRequestDto requestDto;
        try {
            requestDto = mapper.readValue(propertyJson, PropertyRequestDto.class);
            Set<ConstraintViolation<PropertyRequestDto>> violations = validator.validate(requestDto);
            if (!violations.isEmpty()) {
                throw new FailedRequestParsingException("Invalid property data");
            }
        } catch (IOException e) {
            throw new InvalidPropertyDataException("Invalid property data");
        }
        Long userId = getAuthenticatedUserId();
        PropertyResponseDto updatedProperty = propertyService.updateProperty(
                propertyId, requestDto, userId, images);
        return ResponseEntity.ok(updatedProperty);
    }

    @PreAuthorize("hasRole('ROLE_HOST')")
    @DeleteMapping("/delete/{propertyId}")
    @Operation(summary = "Delete an existing property",
            description = """
                    Detailed description of the delete property endpoint:
                    - **Endpoint**: `/api/properties/delete/{propertyId}`
                    - **Path Variable**: `propertyId` (ID of the property to be deleted)
                    
                    1. The user must be authenticated and have the `ROLE_HOST` role to access this endpoint.
                    2. The server retrieves the authenticated user's ID from the security context.
                    3. The server checks if the property exists and if the user has permission to delete it.
                    4. The server deletes the property from the database.
                    5. The server returns a response indicating the deletion was successful (noContent).
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Property deleted successfully"),
                    @ApiResponse(responseCode = "403", description = "User does not have permission to delete a property",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class))),
                    @ApiResponse(responseCode = "404", description = "User/Property not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
            }
    )
    public ResponseEntity<Void> deleteProperty(@PathVariable Long propertyId) {
        Long userId = getAuthenticatedUserId();
        propertyService.deleteProperty(propertyId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an existing property by ID",
            description = """
                    Detailed description of the get property by ID endpoint:
                    - **Endpoint**: `/api/properties/{id}`
                    - **Method**: `GET`
                    - **Path Variable**: `id` (ID of the property to be retrieved)
                    
                    1. The user does not need to be authenticated to access this endpoint.
                    2. The server retrieves the property by its ID.
                    3. The server returns a response with the property details.
                    4. If the property is not found, the server returns a 404 error.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Property found"),
                    @ApiResponse(responseCode = "404", description = "Property not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObject.class)))
            }
    )
    public ResponseEntity<PropertyResponseDto> getPropertyById(@PathVariable Long id) {
        PropertyResponseDto property = propertyService.getPropertyById(id);
        return ResponseEntity.ok(property);
    }

    @GetMapping("/available")
    @Operation(summary = "Get a list of available properties",
            description = """
                    Detailed description of the get available properties endpoint:
                    - **Endpoint**: `/api/properties/available`
                    - **Method**: `GET`
                    - **Request Body**: None
                    
                    1. The user does not need to be authenticated to access this endpoint.
                    2. The server retrieves a paginated list of available properties.
                    3. The server returns a response with the list of available properties.
                    4. The user can specify the page and size of the results using query parameters.
                    5. If no properties are found, the server returns an empty list.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Available properties found")
            }
    )
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
