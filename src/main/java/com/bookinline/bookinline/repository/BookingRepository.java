package com.bookinline.bookinline.repository;

import com.bookinline.bookinline.entity.Booking;
import com.bookinline.bookinline.entity.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Optional<Booking> findById(Long id);
    Page<Booking> findByGuestId(Long guestId, Pageable pageable);
    Page<Booking> findByPropertyId(Long propertyId, Pageable pageable);
    @Query("SELECT b FROM Booking b WHERE b.property.id = :propertyId AND b.guest.id = :guestId AND b.status = :status")
    List<Booking> findByPropertyIdAndGuestIdAndStatus(@Param("propertyId") Long propertyId,
                                                      @Param("guestId") Long guestId,
                                                      @Param("status") BookingStatus status);
    @Query("SELECT b FROM Booking b WHERE b.status = :status AND b.checkOutDate < :checkOutDate")
    List<Booking> findByStatusAndCheckOutDateBefore(@Param("status") BookingStatus status,
                                                    @Param("checkOutDate") LocalDate checkOutDate);
    @Query("SELECT b FROM Booking b WHERE b.property.id = :propertyId AND b.status IN :statuses")
    List<Booking> findByPropertyIdAndStatuses(@Param("propertyId") Long propertyId, @Param("statuses") List<BookingStatus> statuses);
}
