package com.sasps.bookingservice.controller;

import com.sasps.bookingservice.dto.BookingDto;
import com.sasps.bookingservice.service.BookingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Booking Management", description = "APIs for managing hotel bookings")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    @Operation(summary = "Get all bookings", description = "Retrieve all bookings")
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        log.info("GET /api/bookings - Get all bookings");
        List<BookingDto> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID", description = "Retrieve a specific booking by its ID")
    public ResponseEntity<BookingDto> getBookingById(
            @Parameter(description = "Booking ID") @PathVariable Long id) {
        log.info("GET /api/bookings/{} - Get booking by ID", id);
        BookingDto booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/confirmation/{confirmationNumber}")
    @Operation(summary = "Get booking by confirmation number", description = "Retrieve booking by confirmation number")
    public ResponseEntity<BookingDto> getBookingByConfirmationNumber(
            @Parameter(description = "Confirmation number") @PathVariable String confirmationNumber) {
        log.info("GET /api/bookings/confirmation/{} - Get booking by confirmation number", confirmationNumber);
        BookingDto booking = bookingService.getBookingByConfirmationNumber(confirmationNumber);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user bookings", description = "Retrieve all bookings for a specific user")
    public ResponseEntity<List<BookingDto>> getUserBookings(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.info("GET /api/bookings/user/{} - Get user bookings", userId);
        List<BookingDto> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/user/{userId}/active")
    @Operation(summary = "Get user active bookings", description = "Retrieve active bookings for a user")
    public ResponseEntity<List<BookingDto>> getUserActiveBookings(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.info("GET /api/bookings/user/{}/active - Get user active bookings", userId);
        List<BookingDto> bookings = bookingService.getUserActiveBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/room/{roomId}")
    @Operation(summary = "Get room bookings", description = "Retrieve all bookings for a specific room")
    public ResponseEntity<List<BookingDto>> getRoomBookings(
            @Parameter(description = "Room ID") @PathVariable Long roomId) {
        log.info("GET /api/bookings/room/{} - Get room bookings", roomId);
        List<BookingDto> bookings = bookingService.getRoomBookings(roomId);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping
    @Operation(summary = "Create booking", description = "Create a new booking")
    public ResponseEntity<BookingDto> createBooking(@Valid @RequestBody BookingDto.CreateRequest request) {
        log.info("POST /api/bookings - Create booking");
        BookingDto createdBooking = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update booking", description = "Update an existing booking")
    public ResponseEntity<BookingDto> updateBooking(
            @Parameter(description = "Booking ID") @PathVariable Long id,
            @Valid @RequestBody BookingDto.UpdateRequest request) {
        log.info("PUT /api/bookings/{} - Update booking", id);
        BookingDto updatedBooking = bookingService.updateBooking(id, request);
        return ResponseEntity.ok(updatedBooking);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel booking", description = "Cancel an existing booking")
    public ResponseEntity<BookingDto> cancelBooking(
            @Parameter(description = "Booking ID") @PathVariable Long id,
            @Valid @RequestBody BookingDto.CancelRequest request) {
        log.info("POST /api/bookings/{}/cancel - Cancel booking", id);
        BookingDto cancelledBooking = bookingService.cancelBooking(id, request.getCancellationReason());
        return ResponseEntity.ok(cancelledBooking);
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirm booking", description = "Confirm a pending booking after payment")
    public ResponseEntity<BookingDto> confirmBooking(
            @Parameter(description = "Booking ID") @PathVariable Long id,
            @Valid @RequestBody BookingDto.ConfirmRequest request) {
        log.info("POST /api/bookings/{}/confirm - Confirm booking", id);
        BookingDto confirmedBooking = bookingService.confirmBooking(id, request);
        return ResponseEntity.ok(confirmedBooking);
    }

    @PostMapping("/{id}/check-in")
    @Operation(summary = "Check-in booking", description = "Check-in a confirmed booking")
    public ResponseEntity<BookingDto> checkInBooking(
            @Parameter(description = "Booking ID") @PathVariable Long id) {
        log.info("POST /api/bookings/{}/check-in - Check-in booking", id);
        BookingDto checkedInBooking = bookingService.checkInBooking(id);
        return ResponseEntity.ok(checkedInBooking);
    }

    @PostMapping("/{id}/check-out")
    @Operation(summary = "Check-out booking", description = "Check-out a checked-in booking")
    public ResponseEntity<BookingDto> checkOutBooking(
            @Parameter(description = "Booking ID") @PathVariable Long id) {
        log.info("POST /api/bookings/{}/check-out - Check-out booking", id);
        BookingDto checkedOutBooking = bookingService.checkOutBooking(id);
        return ResponseEntity.ok(checkedOutBooking);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete booking", description = "Delete a booking")
    public ResponseEntity<Void> deleteBooking(
            @Parameter(description = "Booking ID") @PathVariable Long id) {
        log.info("DELETE /api/bookings/{} - Delete booking", id);
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
