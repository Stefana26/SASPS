package com.sasps.hotelbooking.controller;

import com.sasps.hotelbooking.dto.BookingDto;
import com.sasps.hotelbooking.service.BookingService;
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
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Booking Management", description = "APIs for managing hotel bookings")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    @Operation(summary = "Get all bookings", description = "Retrieve a list of all bookings")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of bookings"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        log.info("GET /api/bookings - Get all bookings");
        List<BookingDto> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID", description = "Retrieve a specific booking by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved booking"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    public ResponseEntity<BookingDto> getBookingById(
            @Parameter(description = "Booking ID", required = true)
            @PathVariable Long id) {
        log.info("GET /api/bookings/{} - Get booking by ID", id);
        BookingDto booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/confirmation/{confirmationNumber}")
    @Operation(summary = "Get booking by confirmation number", 
               description = "Retrieve a booking by its confirmation number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved booking"),
        @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<BookingDto> getBookingByConfirmationNumber(
            @Parameter(description = "Confirmation number", required = true)
            @PathVariable String confirmationNumber) {
        log.info("GET /api/bookings/confirmation/{} - Get booking by confirmation number", 
                confirmationNumber);
        BookingDto booking = bookingService.getBookingByConfirmationNumber(confirmationNumber);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user bookings", description = "Retrieve all bookings for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved bookings"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<BookingDto>> getUserBookings(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId) {
        log.info("GET /api/bookings/user/{} - Get user bookings", userId);
        List<BookingDto> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/user/{userId}/active")
    @Operation(summary = "Get user active bookings", 
               description = "Retrieve all active bookings for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved active bookings"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<BookingDto>> getUserActiveBookings(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId) {
        log.info("GET /api/bookings/user/{}/active - Get user active bookings", userId);
        List<BookingDto> bookings = bookingService.getUserActiveBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/room/{roomId}")
    @Operation(summary = "Get room bookings", description = "Retrieve all bookings for a specific room")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved bookings"),
        @ApiResponse(responseCode = "404", description = "Room not found")
    })
    public ResponseEntity<List<BookingDto>> getRoomBookings(
            @Parameter(description = "Room ID", required = true)
            @PathVariable Long roomId) {
        log.info("GET /api/bookings/room/{} - Get room bookings", roomId);
        List<BookingDto> bookings = bookingService.getRoomBookings(roomId);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping
    @Operation(summary = "Create new booking", description = "Create a new booking in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Booking created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or validation error"),
        @ApiResponse(responseCode = "404", description = "User or Room not found"),
        @ApiResponse(responseCode = "409", description = "Room not available for selected dates")
    })
    public ResponseEntity<BookingDto> createBooking(
            @Parameter(description = "Booking creation request", required = true)
            @Valid @RequestBody BookingDto.CreateRequest request) {
        log.info("POST /api/bookings - Create new booking");
        BookingDto createdBooking = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update booking", description = "Update an existing booking")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Booking updated successfully"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input or business rule violation")
    })
    public ResponseEntity<BookingDto> updateBooking(
            @Parameter(description = "Booking ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Booking update request", required = true)
            @Valid @RequestBody BookingDto.UpdateRequest request) {
        log.info("PUT /api/bookings/{} - Update booking", id);
        BookingDto updatedBooking = bookingService.updateBooking(id, request);
        return ResponseEntity.ok(updatedBooking);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel booking", description = "Cancel an existing booking")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Booking cancelled successfully"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "400", description = "Booking cannot be cancelled in current status")
    })
    public ResponseEntity<BookingDto> cancelBooking(
            @Parameter(description = "Booking ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Cancellation request", required = true)
            @Valid @RequestBody BookingDto.CancelRequest request) {
        log.info("POST /api/bookings/{}/cancel - Cancel booking", id);
        BookingDto cancelledBooking = bookingService.cancelBooking(id, request);
        return ResponseEntity.ok(cancelledBooking);
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirm booking", description = "Confirm a pending booking after payment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Booking confirmed successfully"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "400", description = "Only pending bookings can be confirmed")
    })
    public ResponseEntity<BookingDto> confirmBooking(
            @Parameter(description = "Booking ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Confirmation request", required = true)
            @Valid @RequestBody BookingDto.ConfirmRequest request) {
        log.info("POST /api/bookings/{}/confirm - Confirm booking", id);
        BookingDto confirmedBooking = bookingService.confirmBooking(id, request);
        return ResponseEntity.ok(confirmedBooking);
    }

    @PostMapping("/{id}/check-in")
    @Operation(summary = "Check-in booking", description = "Check-in a confirmed booking")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Booking checked in successfully"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "400", description = "Only confirmed bookings can be checked in")
    })
    public ResponseEntity<BookingDto> checkInBooking(
            @Parameter(description = "Booking ID", required = true)
            @PathVariable Long id) {
        log.info("POST /api/bookings/{}/check-in - Check-in booking", id);
        BookingDto checkedInBooking = bookingService.checkInBooking(id);
        return ResponseEntity.ok(checkedInBooking);
    }

    @PostMapping("/{id}/check-out")
    @Operation(summary = "Check-out booking", description = "Check-out a checked-in booking")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Booking checked out successfully"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "400", description = "Only checked-in bookings can be checked out")
    })
    public ResponseEntity<BookingDto> checkOutBooking(
            @Parameter(description = "Booking ID", required = true)
            @PathVariable Long id) {
        log.info("POST /api/bookings/{}/check-out - Check-out booking", id);
        BookingDto checkedOutBooking = bookingService.checkOutBooking(id);
        return ResponseEntity.ok(checkedOutBooking);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete booking", description = "Delete a booking from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Booking deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "400", description = "Cannot delete active booking")
    })
    public ResponseEntity<Void> deleteBooking(
            @Parameter(description = "Booking ID", required = true)
            @PathVariable Long id) {
        log.info("DELETE /api/bookings/{} - Delete booking", id);
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
