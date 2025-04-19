package repositories;

import entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Optional<Booking> findById(Long id);
    Page<Booking> findByGuestId(Long guestId, Pageable pageable);
    Page<Booking> findByPropertyId(Long propertyId, Pageable pageable);
}
