package com.sasps.bookingservice.repository;

import com.sasps.bookingservice.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    Optional<Booking> findByConfirmationNumber(String confirmationNumber);
    
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<Booking> findByRoomIdOrderByCheckInDateAsc(Long roomId);
    
    @Query("SELECT b FROM Booking b WHERE b.userId = :userId AND b.status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN')")
    List<Booking> findActiveBookingsByUserId(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN false ELSE true END FROM Booking b " +
           "WHERE b.roomId = :roomId " +
           "AND b.status NOT IN ('CANCELLED', 'CHECKED_OUT', 'NO_SHOW') " +
           "AND ((b.checkInDate <= :checkOutDate AND b.checkOutDate >= :checkInDate))")
    boolean isRoomAvailable(@Param("roomId") Long roomId, 
                           @Param("checkInDate") LocalDate checkInDate,
                           @Param("checkOutDate") LocalDate checkOutDate);
    
    @Query("SELECT b FROM Booking b WHERE b.roomId = :roomId " +
           "AND b.status NOT IN ('CANCELLED', 'NO_SHOW') " +
           "AND ((b.checkInDate <= :checkOutDate AND b.checkOutDate >= :checkInDate))")
    List<Booking> findOverlappingBookings(@Param("roomId") Long roomId,
                                          @Param("checkInDate") LocalDate checkInDate,
                                          @Param("checkOutDate") LocalDate checkOutDate);
}

