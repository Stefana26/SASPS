package com.sasps.hotelbooking.repository;

import com.sasps.hotelbooking.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    Optional<Hotel> findByName(String name);

    List<Hotel> findByCity(String city);

    List<Hotel> findByCountry(String country);

    List<Hotel> findByActiveTrue();

    @Query("SELECT DISTINCT h FROM Hotel h JOIN h.rooms r WHERE r.status = 'AVAILABLE' AND h.active = true")
    List<Hotel> findHotelsWithAvailableRooms();
}
