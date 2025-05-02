package com.bookinline.bookinline.service.impl;

import com.bookinline.bookinline.dto.PropertyRequestDto;
import com.bookinline.bookinline.dto.PropertyResponseDto;
import com.bookinline.bookinline.dto.PropertyResponsePage;
import com.bookinline.bookinline.entity.Image;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.exception.PropertyNotFoundException;
import com.bookinline.bookinline.exception.UnauthorizedActionException;
import com.bookinline.bookinline.exception.UserNotFoundException;
import com.bookinline.bookinline.mapper.PropertyMapper;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.service.ImageService;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.service.PropertyService;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class PropertyServiceImpl implements PropertyService {
    private static final Logger logger = LoggerFactory.getLogger(PropertyServiceImpl.class);

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;

    public PropertyServiceImpl(PropertyRepository propertyRepository,
                               UserRepository userRepository,
                               ImageService imageService) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.imageService = imageService;
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

        List<Image> imageList = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            logger.info("Uploading images for property with ID: {}", property.getId());
            for (MultipartFile file : images) {
                String imageUrl = imageService.uploadImage(file);
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
        property.setAddress(propertyRequestDto.getAddress());
        property.setPricePerNight(propertyRequestDto.getPricePerNight());
        property.setMaxGuests(propertyRequestDto.getMaxGuests());

        List<Image> imageList = property.getImages();
        imageList.clear();
        if (images != null && !images.isEmpty()) {
            logger.info("Uploading images for property with ID: {}", propertyId);
            for (MultipartFile file : images) {
                String imageUrl = imageService.uploadImage(file);
                Image image = new Image();
                image.setImageUrl(imageUrl);
                image.setProperty(property);
                imageList.add(image);
            }
        }
        property.setImages(imageList);
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
}
