package repositories;

import entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Page<Review> findByPropertyId(Long propertyId);
    Page<Review> findByAuthorId(Long authorId);
    Optional<Review> findById(Long id);
}
