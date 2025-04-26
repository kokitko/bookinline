package com.bookinline.bookinline.repository;

import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.Role;
import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Flyway flyway;

    User user1 = new User();
    User user2 = new User();

    @BeforeEach
    public void setup() {
        flyway.clean();
        flyway.migrate();

        user1.setFullName("John Doe");
        user1.setEmail("johndoe88@gmail.com");
        user1.setPassword("password123");
        user1.setPhoneNumber("1234567890");
        user1.setRole(Role.GUEST);

        user2.setFullName("Jane Doe");
        user2.setEmail("janedoe91@gmail.com");
        user2.setPassword("password456");
        user2.setPhoneNumber("0987654321");
        user2.setRole(Role.HOST);
    }

    @Test
    public void UserRepository_FindByEmail_ReturnsUser() {
        userRepository.saveAll(List.of(user1, user2));
        User foundUser = userRepository.findByEmail(user1.getEmail()).orElse(null);
        Assertions.assertThat(foundUser).isNotNull();
        Assertions.assertThat(foundUser.getEmail()).isEqualTo(user1.getEmail());
    }

    @Test
    public void UserRepository_FindById_ReturnsUser() {
        userRepository.saveAll(List.of(user1, user2));
        User foundUser = userRepository.findById(user1.getId()).orElse(null);
        Assertions.assertThat(foundUser).isNotNull();
        Assertions.assertThat(foundUser.getId()).isEqualTo(user1.getId());
    }

    @Test
    public void UserRepository_ExistsByEmail_ReturnsTrue() {
        userRepository.saveAll(List.of(user1, user2));
        boolean exists = userRepository.existsByEmail(user1.getEmail());
        Assertions.assertThat(exists).isTrue();
    }
}
