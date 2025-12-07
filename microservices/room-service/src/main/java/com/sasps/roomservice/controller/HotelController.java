package com.sasps.roomservice.controller;

import com.sasps.roomservice.service.HotelService;
import com.sasps.roomservice.dto.HotelDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Hotel Management", description = "APIs for managing hotels")
public class HotelController {

    private final HotelService hotelService;

    @GetMapping
    @Operation(summary = "Get all hotels", description = "Retrieve a list of all hotels")
    public ResponseEntity<List<HotelDto>> getAllHotels(
            @Parameter(description = "Only active hotels")
            @RequestParam(required = false, defaultValue = "true") Boolean activeOnly) {
        log.info("GET /api/hotels - Get all hotels (activeOnly: {})", activeOnly);
        List<HotelDto> hotels = activeOnly ? hotelService.getAllActiveHotels() : hotelService.getAllHotels();
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get hotel by ID", description = "Retrieve a specific hotel by its ID")
    public ResponseEntity<HotelDto> getHotelById(
            @Parameter(description = "Hotel ID") @PathVariable Long id) {
        log.info("GET /api/hotels/{} - Get hotel by ID", id);
        HotelDto hotel = hotelService.getHotelById(id);
        return ResponseEntity.ok(hotel);
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Get hotels by city", description = "Retrieve all hotels in a specific city")
    public ResponseEntity<List<HotelDto>> getHotelsByCity(
            @Parameter(description = "City name") @PathVariable String city) {
        log.info("GET /api/hotels/city/{} - Get hotels by city", city);
        List<HotelDto> hotels = hotelService.getHotelsByCity(city);
        return ResponseEntity.ok(hotels);
    }

    @PostMapping
    @Operation(summary = "Create hotel", description = "Create a new hotel in the system")
    public ResponseEntity<HotelDto> createHotel(@Valid @RequestBody HotelDto.CreateRequest request) {
        log.info("POST /api/hotels - Create hotel");
        HotelDto createdHotel = hotelService.createHotel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdHotel);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update hotel", description = "Update an existing hotel's information")
    public ResponseEntity<HotelDto> updateHotel(
            @Parameter(description = "Hotel ID") @PathVariable Long id,
            @Valid @RequestBody HotelDto.UpdateRequest request) {
        log.info("PUT /api/hotels/{} - Update hotel", id);
        HotelDto updatedHotel = hotelService.updateHotel(id, request);
        return ResponseEntity.ok(updatedHotel);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete hotel", description = "Delete a hotel from the system")
    public ResponseEntity<Void> deleteHotel(
            @Parameter(description = "Hotel ID") @PathVariable Long id) {
        log.info("DELETE /api/hotels/{} - Delete hotel", id);
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    @Operation(summary = "Search hotels", description = "Search for hotels based on various criteria")
    public ResponseEntity<List<HotelDto>> searchHotels(
            @Valid @RequestBody HotelDto.SearchRequest searchRequest) {
        log.info("POST /api/hotels/search - Search hotels");
        List<HotelDto> hotels = hotelService.searchHotels(searchRequest);
        return ResponseEntity.ok(hotels);
    }
}

