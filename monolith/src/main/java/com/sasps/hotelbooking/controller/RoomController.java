package com.sasps.hotelbooking.controller;

import com.sasps.hotelbooking.dto.RoomDto;
import com.sasps.hotelbooking.model.Room;
import com.sasps.hotelbooking.service.RoomService;
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
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Room Management", description = "APIs for managing hotel rooms")
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/hotel/{hotelId}")
    @Operation(summary = "Get rooms by hotel", description = "Retrieve all rooms for a specific hotel")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved rooms"),
        @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    public ResponseEntity<List<RoomDto>> getRoomsByHotel(
            @Parameter(description = "Hotel ID", required = true)
            @PathVariable Long hotelId) {
        log.info("GET /api/rooms/hotel/{} - Get rooms by hotel", hotelId);
        List<RoomDto> rooms = roomService.getRoomsByHotel(hotelId);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/hotel/{hotelId}/number/{roomNumber}")
    @Operation(summary = "Get room by hotel and room number", 
               description = "Retrieve a specific room by hotel ID and room number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved room"),
        @ApiResponse(responseCode = "404", description = "Room not found")
    })
    public ResponseEntity<RoomDto> getRoomByHotelAndNumber(
            @Parameter(description = "Hotel ID", required = true)
            @PathVariable Long hotelId,
            @Parameter(description = "Room number", required = true)
            @PathVariable String roomNumber) {
        log.info("GET /api/rooms/hotel/{}/number/{} - Get room by hotel and room number", hotelId, roomNumber);
        RoomDto room = roomService.getRoomByRoomNumberAndHotel(roomNumber, hotelId);
        return ResponseEntity.ok(room);
    }

    @PostMapping
    @Operation(summary = "Create new room", description = "Create a new room in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Room created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or validation error"),
        @ApiResponse(responseCode = "409", description = "Room number already exists")
    })
    public ResponseEntity<RoomDto> createRoom(
            @Parameter(description = "Room creation request", required = true)
            @Valid @RequestBody RoomDto.CreateRequest request) {
        log.info("POST /api/rooms - Create new room");
        RoomDto createdRoom = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update room", description = "Update an existing room's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Room updated successfully"),
        @ApiResponse(responseCode = "404", description = "Room not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input or validation error"),
        @ApiResponse(responseCode = "409", description = "Room number already exists")
    })
    public ResponseEntity<RoomDto> updateRoom(
            @Parameter(description = "Room ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Room update request", required = true)
            @Valid @RequestBody RoomDto.UpdateRequest request) {
        log.info("PUT /api/rooms/{} - Update room", id);
        RoomDto updatedRoom = roomService.updateRoom(id, request);
        return ResponseEntity.ok(updatedRoom);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete room", description = "Delete a room from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Room deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Room not found")
    })
    public ResponseEntity<Void> deleteRoom(
            @Parameter(description = "Room ID", required = true)
            @PathVariable Long id) {
        log.info("DELETE /api/rooms/{} - Delete room", id);
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{roomType}")
    @Operation(summary = "Get rooms by type", description = "Retrieve all rooms of a specific type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved rooms"),
        @ApiResponse(responseCode = "400", description = "Invalid room type")
    })
    public ResponseEntity<List<RoomDto>> getRoomsByType(
            @Parameter(description = "Room type", required = true)
            @PathVariable Room.RoomType roomType) {
        log.info("GET /api/rooms/type/{} - Get rooms by type", roomType);
        List<RoomDto> rooms = roomService.getRoomsByType(roomType);
        return ResponseEntity.ok(rooms);
    }

    @PostMapping("/search")
    @Operation(summary = "Search available rooms", 
               description = "Search for available rooms based on dates and other criteria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved available rooms"),
        @ApiResponse(responseCode = "400", description = "Invalid search criteria")
    })
    public ResponseEntity<List<RoomDto>> searchAvailableRooms(
            @Parameter(description = "Search criteria", required = true)
            @Valid @RequestBody RoomDto.SearchRequest searchRequest) {
        log.info("POST /api/rooms/search - Search available rooms");
        List<RoomDto> rooms = roomService.searchAvailableRooms(searchRequest);
        return ResponseEntity.ok(rooms);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update room status", description = "Update the status of a specific room")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Room status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Room not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status")
    })
    public ResponseEntity<RoomDto> updateRoomStatus(
            @Parameter(description = "Room ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "New status", required = true)
            @RequestParam Room.RoomStatus status) {
        log.info("PATCH /api/rooms/{}/status - Update room status to {}", id, status);
        RoomDto updatedRoom = roomService.updateRoomStatus(id, status);
        return ResponseEntity.ok(updatedRoom);
    }
}
