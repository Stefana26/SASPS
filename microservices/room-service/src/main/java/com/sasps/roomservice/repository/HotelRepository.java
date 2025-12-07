package com.sasps.roomservice.repository;

import com.sasps.roomservice.model.Hotel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByActiveTrue();
    
    List<Hotel> findByCity(String city);
}

