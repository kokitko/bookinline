package com.bookinline.bookinline.repository;

import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    boolean existsByEmail(String email);
    Page<User> findUsersByStatus(UserStatus status, Pageable pageable);
}
