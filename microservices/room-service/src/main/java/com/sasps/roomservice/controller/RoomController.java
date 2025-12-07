package com.sasps.roomservice.controller;

import com.sasps.roomservice.dto.RoomDto;
import com.sasps.roomservice.service.RoomService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Room Management", description = "APIs for managing rooms")
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/{id}")
    @Operation(summary = "Get room by ID", description = "Retrieve a specific room by its ID")
    public ResponseEntity<RoomDto> getRoomById(
            @Parameter(description = "Room ID") @PathVariable Long id) {
        log.info("GET /api/rooms/{} - Get room by ID", id);
        RoomDto room = roomService.getRoomById(id);
        return ResponseEntity.ok(room);
    }

    @GetMapping("/hotel/{hotelId}")
    @Operation(summary = "Get rooms by hotel", description = "Retrieve all rooms for a specific hotel")
    public ResponseEntity<List<RoomDto>> getRoomsByHotel(
            @Parameter(description = "Hotel ID") @PathVariable Long hotelId) {
        log.info("GET /api/rooms/hotel/{} - Get rooms by hotel", hotelId);
        List<RoomDto> rooms = roomService.getRoomsByHotel(hotelId);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/hotel/{hotelId}/number/{roomNumber}")
    @Operation(summary = "Get room by hotel and room number", description = "Retrieve a specific room by hotel ID and room number")
    public ResponseEntity<RoomDto> getRoomByHotelAndNumber(
            @Parameter(description = "Hotel ID") @PathVariable Long hotelId,
            @Parameter(description = "Room number") @PathVariable String roomNumber) {
        log.info("GET /api/rooms/hotel/{}/number/{} - Get room by hotel and room number", hotelId, roomNumber);
        RoomDto room = roomService.getRoomByRoomNumberAndHotel(roomNumber, hotelId);
        return ResponseEntity.ok(room);
    }

    @PostMapping
    @Operation(summary = "Create room", description = "Create a new room in the system")
    public ResponseEntity<RoomDto> createRoom(@Valid @RequestBody RoomDto.CreateRequest request) {
        log.info("POST /api/rooms - Create room");
        RoomDto createdRoom = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update room", description = "Update an existing room's information")
    public ResponseEntity<RoomDto> updateRoom(
            @Parameter(description = "Room ID") @PathVariable Long id,
            @Valid @RequestBody RoomDto.UpdateRequest request) {
        log.info("PUT /api/rooms/{} - Update room", id);
        RoomDto updatedRoom = roomService.updateRoom(id, request);
        return ResponseEntity.ok(updatedRoom);
    }

    @GetMapping("/type/{roomType}")
    @Operation(summary = "Get rooms by type", description = "Retrieve all rooms of a specific type")
    public ResponseEntity<List<RoomDto>> getRoomsByType(
            @Parameter(description = "Room type") @PathVariable String roomType) {
        log.info("GET /api/rooms/type/{} - Get rooms by type", roomType);
        List<RoomDto> rooms = roomService.getRoomsByType(roomType);
        return ResponseEntity.ok(rooms);
    }

    @PostMapping("/search")
    @Operation(summary = "Search available rooms", description = "Search for available rooms based on dates and other criteria")
    public ResponseEntity<List<RoomDto>> searchAvailableRooms(
            @Valid @RequestBody RoomDto.SearchRequest searchRequest) {
        log.info("POST /api/rooms/search - Search available rooms");
        List<RoomDto> rooms = roomService.searchAvailableRooms(searchRequest);
        return ResponseEntity.ok(rooms);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update room status", description = "Update the status of a specific room")
    public ResponseEntity<RoomDto> updateRoomStatus(
            @Parameter(description = "Room ID") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam String status) {
        log.info("PATCH /api/rooms/{}/status - Update room status to {}", id, status);
        RoomDto updatedRoom = roomService.updateRoomStatus(id, status);
        return ResponseEntity.ok(updatedRoom);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete room", description = "Delete a room from the system")
    public ResponseEntity<Void> deleteRoom(
            @Parameter(description = "Room ID") @PathVariable Long id) {
        log.info("DELETE /api/rooms/{} - Delete room", id);
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}

