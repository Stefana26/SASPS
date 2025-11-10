package com.sasps.hotelbooking.repository;

import com.sasps.hotelbooking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByConfirmationNumber(String confirmationNumber);

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Booking> findByRoomIdOrderByCheckInDateAsc(Long roomId);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND " +
           "b.status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN') " +
           "ORDER BY b.checkInDate ASC")
    List<Booking> findActiveBookingsByUserId(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.checkInDate > :currentDate AND " +
           "b.status IN ('PENDING', 'CONFIRMED') " +
           "ORDER BY b.checkInDate ASC")
    List<Booking> findUpcomingBookings(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT b FROM Booking b WHERE b.checkInDate = :date AND " +
           "b.status = 'CONFIRMED' " +
           "ORDER BY b.createdAt ASC")
    List<Booking> findBookingsForCheckInToday(@Param("date") LocalDate date);

    @Query("SELECT b FROM Booking b WHERE b.checkOutDate = :date AND " +
           "b.status = 'CHECKED_IN' " +
           "ORDER BY b.createdAt ASC")
    List<Booking> findBookingsForCheckOutToday(@Param("date") LocalDate date);

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND " +
           "b.status IN ('CONFIRMED', 'CHECKED_IN', 'PENDING') AND " +
           "((b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn))")
    List<Booking> findOverlappingBookings(@Param("roomId") Long roomId,
                                          @Param("checkIn") LocalDate checkIn,
                                          @Param("checkOut") LocalDate checkOut);

    @Query("SELECT COUNT(b) = 0 FROM Booking b WHERE b.room.id = :roomId AND " +
           "b.status IN ('CONFIRMED', 'CHECKED_IN', 'PENDING') AND " +
           "((b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn))")
    boolean isRoomAvailable(@Param("roomId") Long roomId,
                           @Param("checkIn") LocalDate checkIn,
                           @Param("checkOut") LocalDate checkOut);
}
