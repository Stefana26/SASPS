package com.sasps.hotelbooking.repository;

import com.sasps.hotelbooking.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomNumberAndHotelId(String roomNumber, Long hotelId);

    boolean existsByRoomNumberAndHotelId(String roomNumber, Long hotelId);

    List<Room> findByHotelId(Long hotelId);

    List<Room> findByRoomType(Room.RoomType roomType);


    List<Room> findByStatus(Room.RoomStatus status);

    @Query("SELECT r FROM Room r WHERE r.status = 'AVAILABLE' AND r.id NOT IN " +
           "(SELECT b.room.id FROM Booking b WHERE " +
           "b.status IN ('CONFIRMED', 'CHECKED_IN', 'PENDING') AND " +
           "((b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn)))")
    List<Room> findAvailableRoomsForDates(@Param("checkIn") LocalDate checkIn, 
                                           @Param("checkOut") LocalDate checkOut);

    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId AND r.status = 'AVAILABLE' AND r.id NOT IN " +
           "(SELECT b.room.id FROM Booking b WHERE " +
           "b.status IN ('CONFIRMED', 'CHECKED_IN', 'PENDING') AND " +
           "((b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn)))")
    List<Room> findAvailableRoomsForHotelAndDates(@Param("hotelId") Long hotelId,
                                                   @Param("checkIn") LocalDate checkIn,
                                           @Param("checkOut") LocalDate checkOut);

    @Query("SELECT r FROM Room r WHERE r.status = 'AVAILABLE' AND r.roomType = :roomType AND r.id NOT IN " +
           "(SELECT b.room.id FROM Booking b WHERE " +
           "b.status IN ('CONFIRMED', 'CHECKED_IN', 'PENDING') AND " +
           "((b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn)))")
    List<Room> findAvailableRoomsByTypeForDates(@Param("roomType") Room.RoomType roomType,
                                                 @Param("checkIn") LocalDate checkIn,
                                                 @Param("checkOut") LocalDate checkOut);


    long countByStatus(Room.RoomStatus status);
}
