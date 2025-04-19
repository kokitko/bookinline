package com.bookinline.bookinline.repositories;

import com.bookinline.bookinline.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Integer> {
    Page<Property> findByAvailableTrue(Pageable pageable);
    Optional<Property> findById(Long id);
}
