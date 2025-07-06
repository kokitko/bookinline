package com.bookinline.bookinline.repository;

import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.enums.PropertyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Integer> {
    Page<Property> findByAvailableTrue(Pageable pageable);
    Page<Property> findAll(Specification<Property> specification, Pageable pageable);
    Optional<Property> findById(Long id);
    Page<Property> findByHostId(Long hostId, Pageable pageable);
    Page<Property> findPropertiesByPropertyType(PropertyType propertyType, Pageable pageable);
}
