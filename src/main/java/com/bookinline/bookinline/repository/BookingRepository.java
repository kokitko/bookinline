package com.bookinline.bookinline.repository;

import com.bookinline.bookinline.entity.Booking;
import com.bookinline.bookinline.entity.enums.BookingStatus;
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
    List<Booking> findByPropertyIdAndStatuses(@Param("propertyId") Long propertyId,
                                              @Param("statuses") List<BookingStatus> statuses);
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Booking b " +
            "WHERE b.guest.id = :guestId " +
            "AND b.property.host.id = :hostId " +
            "AND b.status IN :statuses")
    boolean existsByGuestIdAndHostIdAndStatuses(@Param("guestId") Long guestId,
                                                @Param("hostId") Long hostId,
                                                @Param("statuses") List<BookingStatus> statuses);

    @Query("SELECT b FROM Booking b WHERE b.property.host.id = :hostId")
    Page<Booking> findByHostId(@Param("hostId") Long hostId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.property.host.id = :hostId AND b.status = :status")
    Page<Booking> findByHostIdAndStatus(@Param("hostId") Long hostId,
                                        @Param("status") BookingStatus status,
                                        Pageable pageable);
}
