package com.bookinline.bookinline.service.impl;

import com.bookinline.bookinline.dto.PropertyFilterDto;
import com.bookinline.bookinline.dto.PropertyRequestDto;
import com.bookinline.bookinline.dto.PropertyResponseDto;
import com.bookinline.bookinline.dto.PropertyResponsePage;
import com.bookinline.bookinline.entity.Image;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.enums.PropertyType;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.exception.PropertyNotFoundException;
import com.bookinline.bookinline.exception.UnauthorizedActionException;
import com.bookinline.bookinline.exception.UserNotFoundException;
import com.bookinline.bookinline.mapper.PropertyMapper;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.service.S3Service;
import com.bookinline.bookinline.specification.PropertySpecification;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.service.PropertyService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PropertyServiceImpl implements PropertyService {
    private static final Logger logger = LoggerFactory.getLogger(PropertyServiceImpl.class);

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service
            ;

    public PropertyServiceImpl(PropertyRepository propertyRepository,
                               UserRepository userRepository,
                               S3Service s3Service) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.s3Service = s3Service;
    }

    @Timed(
            value = "property.create",
            description = "Time taken to create a property")
    @Override
    public PropertyResponseDto createProperty(PropertyRequestDto propertyRequestDto,
                                              Long userId, List<MultipartFile> images) {
        logger.info("Attempting to create property for user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new UserNotFoundException("User not found");
                });
        Property property = PropertyMapper.mapToPropertyEntity(propertyRequestDto);
        property.setHost(user);
        property.setAvailable(true);
        property.setAverageRating(0.0);

        logger.info("Creating property with title: {}", property.getTitle());
        property = propertyRepository.save(property);

        List<Image> imageList = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            logger.info("Uploading images for property with ID: {}", property.getId());
            for (MultipartFile file : images) {
                String imageUrl;
                try {
                    imageUrl = s3Service.uploadFile(file);
                } catch (IOException e) {
                    logger.error("Error uploading image: {}", e.getMessage());
                    throw new RuntimeException("Failed to upload image", e);
                }
                Image image = new Image();
                image.setImageUrl(imageUrl);
                image.setProperty(property);
                imageList.add(image);
            }
        }
        property.setImages(imageList);

        Property savedProperty = propertyRepository.save(property);
        logger.info("Property created successfully with ID: {}", savedProperty.getId());
        return PropertyMapper.mapToPropertyResponseDto(savedProperty);
    }

    @Timed(
            value = "property.update",
            description = "Time taken to update a property")
    @Override
    public PropertyResponseDto updateProperty(Long propertyId, PropertyRequestDto propertyRequestDto,
                                              Long userId, List<MultipartFile> images) {
        logger.info("Attempting to update property with ID: {} for user with ID: {}", propertyId, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new UserNotFoundException("User not found");
                });
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> {
                    logger.error("Property not found with ID: {}", propertyId);
                    return new PropertyNotFoundException("Property not found");
                });
        if (!property.getHost().equals(user)) {
            logger.warn("User with ID: {} does not have permission to update property with ID: {}", userId, propertyId);
            throw new UnauthorizedActionException("User does not have permission to update this property");
        }
        property.setTitle(propertyRequestDto.getTitle());
        property.setDescription(propertyRequestDto.getDescription());
        property.setCity(propertyRequestDto.getCity());
        property.setPropertyType(PropertyType.valueOf(propertyRequestDto.getPropertyType()));
        property.setFloorArea(propertyRequestDto.getFloorArea());
        property.setBedrooms(propertyRequestDto.getBedrooms());
        property.setAddress(propertyRequestDto.getAddress());
        property.setPricePerNight(propertyRequestDto.getPricePerNight());
        property.setMaxGuests(propertyRequestDto.getMaxGuests());


        if (images != null && !images.isEmpty()) {
            logger.info("Uploading images for property with ID: {}", propertyId);
            List<Image> imageList = property.getImages();
            imageList.clear();
            for (MultipartFile file : images) {
                String imageUrl;
                try {
                    imageUrl = s3Service.uploadFile(file);
                } catch (IOException e) {
                    logger.error("Error uploading image: {}", e.getMessage());
                    throw new RuntimeException("Failed to upload image", e);
                }
                Image image = new Image();
                image.setImageUrl(imageUrl);
                image.setProperty(property);
                imageList.add(image);
                property.setImages(imageList);
            }
        }

        Property updatedProperty = propertyRepository.save(property);

        logger.info("Property with ID: {} updated successfully", propertyId);
        return PropertyMapper.mapToPropertyResponseDto(updatedProperty);
    }

    @Timed(
            value = "property.delete",
            description = "Time taken to delete a property")
    @Override
    public void deleteProperty(Long propertyId, Long userId) {
        logger.info("Attempting to delete property with ID: {} for user with ID: {}", propertyId, userId);
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> {
                    logger.error("Property not found with ID: {}", propertyId);
                    return new PropertyNotFoundException("Property not found");
                });
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new UserNotFoundException("User not found");
                });
        if (!property.getHost().equals(user)) {
            logger.warn("Unauthorized action: User with ID: {} does not have permission to delete property with ID: {}",
                    userId, propertyId);
            throw new UnauthorizedActionException("User does not have permission to delete this property");
        }
        propertyRepository.delete(property);
        logger.info("Property with ID: {} deleted successfully", propertyId);
    }

    @Timed(
            value = "property.getById",
            description = "Time taken to get property by ID")
    @Override
    public PropertyResponseDto getPropertyById(Long id) {
        logger.info("Attempting to get property with ID: {}", id);
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Property not found with ID: {}", id);
                    return new PropertyNotFoundException("Property not found");
                });
        logger.info("Property with ID: {} retrieved successfully", id);
        return PropertyMapper.mapToPropertyResponseDto(property);
    }

    @Timed(
            value = "property.getAll",
            description = "Time taken to get all available properties")
    @Cacheable(value = "availableProperties")
    @Override
    public PropertyResponsePage getAvailableProperties(int page, int size) {
        logger.info("Fetching available properties, page: {}, size: {}", page, size);
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Property> propertyPage = propertyRepository.findByAvailableTrue(pageable);
        logger.info("Found {} available properties", propertyPage.getTotalElements());

        return PropertyMapper.mapToPropertyResponsePage(propertyPage);
    }

    @Timed(
            value = "property.getFilteredProperties",
            description = "Time taken to get customly filtered properties")
    @Override
    public PropertyResponsePage getFilteredProperties(PropertyFilterDto propertyFilterDto, int page, int size) {
        logger.info("Fetching filtered properties with filters: {}, page: {}, size: {}", propertyFilterDto, page, size);
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Specification<Property> specification = PropertySpecification.withAvailability()
                .and(PropertySpecification.availableBetween(propertyFilterDto.getCheckIn(), propertyFilterDto.getCheckOut()))
                .and(new PropertySpecification(propertyFilterDto));
        Page<Property> propertyPage = propertyRepository.findAll(specification, pageable);
        logger.info("Found {} filtered properties", propertyPage.getTotalElements());

        return PropertyMapper.mapToPropertyResponsePage(propertyPage);
    }

    @Timed(
            value = "property.getPropertiesByHostId",
            description = "Time taken to get all properties by host ID")
    @Override
    public PropertyResponsePage getPropertiesByHostId(Long hostId, int page, int size) {
        logger.info("Fetching properties for host with ID: {}, page: {}, size: {}", hostId, page, size);
        User user = userRepository.findById(hostId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", hostId);
                    return new UserNotFoundException("User not found");
                });
        Long userId = user.getId();
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Property> propertyPage = propertyRepository.findByHostId(userId, pageable);
        logger.info("Found {} properties for host with ID: {}", propertyPage.getTotalElements(), hostId);

        return PropertyMapper.mapToPropertyResponsePage(propertyPage);
    }
}
