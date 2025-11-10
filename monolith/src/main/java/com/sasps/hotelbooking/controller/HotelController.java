package com.sasps.hotelbooking.controller;

import com.sasps.hotelbooking.dto.HotelDto;
import com.sasps.hotelbooking.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of hotels"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<HotelDto>> getAllHotels(
            @Parameter(description = "Only active hotels")
            @RequestParam(required = false, defaultValue = "true") Boolean activeOnly) {
        log.info("GET /api/hotels - Get all hotels (activeOnly: {})", activeOnly);
        List<HotelDto> hotels = activeOnly ? hotelService.getAllActiveHotels() : hotelService.getAllHotels();
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get hotel by ID", description = "Retrieve a specific hotel by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved hotel"),
        @ApiResponse(responseCode = "404", description = "Hotel not found"),
        @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    public ResponseEntity<HotelDto> getHotelById(
            @Parameter(description = "Hotel ID", required = true)
            @PathVariable Long id) {
        log.info("GET /api/hotels/{} - Get hotel by ID", id);
        HotelDto hotel = hotelService.getHotelById(id);
        return ResponseEntity.ok(hotel);
    }

    @PostMapping
    @Operation(summary = "Create new hotel", description = "Create a new hotel in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Hotel created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or validation error")
    })
    public ResponseEntity<HotelDto> createHotel(
            @Parameter(description = "Hotel creation request", required = true)
            @Valid @RequestBody HotelDto.CreateRequest request) {
        log.info("POST /api/hotels - Create new hotel");
        HotelDto createdHotel = hotelService.createHotel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdHotel);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update hotel", description = "Update an existing hotel's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hotel updated successfully"),
        @ApiResponse(responseCode = "404", description = "Hotel not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input or validation error")
    })
    public ResponseEntity<HotelDto> updateHotel(
            @Parameter(description = "Hotel ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Hotel update request", required = true)
            @Valid @RequestBody HotelDto.UpdateRequest request) {
        log.info("PUT /api/hotels/{} - Update hotel", id);
        HotelDto updatedHotel = hotelService.updateHotel(id, request);
        return ResponseEntity.ok(updatedHotel);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete hotel", description = "Delete a hotel from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Hotel deleted successfully (cascade deletes rooms)"),
        @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    public ResponseEntity<Void> deleteHotel(
            @Parameter(description = "Hotel ID", required = true)
            @PathVariable Long id) {
        log.info("DELETE /api/hotels/{} - Delete hotel", id);
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Get hotels by city", description = "Retrieve all hotels in a specific city")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved hotels")
    public ResponseEntity<List<HotelDto>> getHotelsByCity(
            @Parameter(description = "City name", required = true)
            @PathVariable String city) {
        log.info("GET /api/hotels/city/{} - Get hotels by city", city);
        List<HotelDto> hotels = hotelService.getHotelsByCity(city);
        return ResponseEntity.ok(hotels);
    }

    @PostMapping("/search")
    @Operation(summary = "Search hotels", 
               description = "Search for hotels based on various criteria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved hotels"),
        @ApiResponse(responseCode = "400", description = "Invalid search criteria")
    })
    public ResponseEntity<List<HotelDto>> searchHotels(
            @Parameter(description = "Search criteria", required = true)
            @Valid @RequestBody HotelDto.SearchRequest searchRequest) {
        log.info("POST /api/hotels/search - Search hotels");
        List<HotelDto> hotels = hotelService.searchHotels(searchRequest);
        return ResponseEntity.ok(hotels);
    }
}
