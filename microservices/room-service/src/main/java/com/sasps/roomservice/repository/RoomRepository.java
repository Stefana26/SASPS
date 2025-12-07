package com.sasps.roomservice.repository;

import com.sasps.roomservice.model.Room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    List<Room> findByHotelId(Long hotelId);
    
    Optional<Room> findByRoomNumberAndHotelId(String roomNumber, Long hotelId);
    
    boolean existsByRoomNumberAndHotelId(String roomNumber, Long hotelId);

    List<Room> findByRoomType(Room.RoomType roomType);
}

