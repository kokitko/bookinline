package com.bookinline.bookinline.integration.repository;

import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.Review;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.entity.enums.PropertyType;
import com.bookinline.bookinline.entity.enums.Role;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.repository.ReviewRepository;
import com.bookinline.bookinline.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
public class ReviewRepositoryTest {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Flyway flyway;

    User guest = new User();
    User host = new User();
    Property property = new Property();
    Review review = new Review();

    @BeforeEach
    public void setup() {
        flyway.clean();
        flyway.migrate();

        guest.setFullName("John Doe");
        guest.setEmail("johndoe88@gmail.com");
        guest.setPassword("password123");
        guest.setPhoneNumber("1234567890");
        guest.setRole(Role.GUEST);

        host.setFullName("Jane Doe");
        host.setEmail("janedoe91@gmail.com");
        host.setPassword("password456");
        host.setPhoneNumber("0987654321");
        host.setRole(Role.HOST);

        userRepository.saveAll(List.of(guest, host));

        property.setTitle("Luxury Villa");
        property.setDescription("A luxury villa with a sea view.");
        property.setCity("Beach City");
        property.setFloorArea(200);
        property.setBedrooms(3);
        property.setPropertyType(PropertyType.VILLA);
        property.setAddress("456 Ocean Ave");
        property.setPricePerNight(new BigDecimal("500.00"));
        property.setMaxGuests(6);
        property.setAvailable(true);
        property.setHost(host);
        propertyRepository.save(property);

        review.setRating(5);
        review.setComment("Amazing stay!");
        review.setCreatedAt(LocalDateTime.now());
        review.setAuthor(guest);
        review.setProperty(property);
    }

    @Test
    public void ReviewRepository_FindByPropertyId_ReturnsPage() {
        reviewRepository.save(review);
        Pageable pageable = Pageable.ofSize(10);
        Page<Review> reviews = reviewRepository.findByPropertyId(property.getId(), pageable);

        Assertions.assertThat(reviews).isNotEmpty();
        Assertions.assertThat(reviews.getContent().get(0).getProperty().getId()).isEqualTo(property.getId());
    }

    @Test
    public void ReviewRepository_FindByAuthorId_ReturnsPage() {
        reviewRepository.save(review);
        Pageable pageable = Pageable.ofSize(10);
        Page<Review> reviews = reviewRepository.findByAuthorId(guest.getId(), pageable);

        Assertions.assertThat(reviews).isNotEmpty();
        Assertions.assertThat(reviews.getContent().get(0).getAuthor().getId()).isEqualTo(guest.getId());
    }

    @Test
    public void ReviewRepository_FindByPropertyId_ReturnsReviews() {
        reviewRepository.save(review);
        List<Review> reviews = reviewRepository.findByPropertyId(property.getId());

        Assertions.assertThat(reviews).isNotEmpty();
        Assertions.assertThat(reviews.get(0).getProperty().getId()).isEqualTo(property.getId());
    }

    @Test
    public void ReviewRepository_FindById_ReturnsReview() {
        reviewRepository.save(review);
        Review foundReview = reviewRepository.findById(review.getId()).orElse(null);

        Assertions.assertThat(foundReview).isNotNull();
        Assertions.assertThat(foundReview.getId()).isEqualTo(review.getId());
    }

    @Test
    public void ReviewRepository_FindByPropertyIdAndAuthorId_ReturnsReviews() {
        reviewRepository.save(review);
        List<Review> reviews = reviewRepository.findByPropertyIdAndAuthorId(property.getId(), guest.getId());

        Assertions.assertThat(reviews).isNotEmpty();
        Assertions.assertThat(reviews.get(0).getProperty().getId()).isEqualTo(property.getId());
        Assertions.assertThat(reviews.get(0).getAuthor().getId()).isEqualTo(guest.getId());
    }
}
