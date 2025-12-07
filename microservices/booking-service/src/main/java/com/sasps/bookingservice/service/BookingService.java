package com.sasps.bookingservice.service;

import com.sasps.bookingservice.client.PaymentServiceClient;
import com.sasps.bookingservice.client.RoomServiceClient;
import com.sasps.bookingservice.dto.BookingDto;
import com.sasps.bookingservice.dto.PaymentDto;
import com.sasps.bookingservice.dto.RoomDto;
import com.sasps.bookingservice.exception.BusinessException;
import com.sasps.bookingservice.exception.ResourceNotFoundException;
import com.sasps.bookingservice.model.Booking;
import com.sasps.bookingservice.repository.BookingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private static final String AVAILABLE = "AVAILABLE";
    
    private final BookingRepository bookingRepository;
    private final RoomServiceClient roomServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    public List<BookingDto> getAllBookings() {
        log.debug("Fetching all bookings");
        return bookingRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public BookingDto getBookingById(Long id) {
        log.debug("Fetching booking with id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        return convertToDto(booking);
    }

    public BookingDto getBookingByConfirmationNumber(String confirmationNumber) {
        log.debug("Fetching booking with confirmation number: {}", confirmationNumber);
        Booking booking = bookingRepository.findByConfirmationNumber(confirmationNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "confirmationNumber", confirmationNumber));
        return convertToDto(booking);
    }

    public List<BookingDto> getUserBookings(Long userId) {
        log.debug("Fetching bookings for user id: {}", userId);
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getUserActiveBookings(Long userId) {
        log.debug("Fetching active bookings for user id: {}", userId);
        return bookingRepository.findActiveBookingsByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getRoomBookings(Long roomId) {
        log.debug("Fetching bookings for room id: {}", roomId);
        return bookingRepository.findByRoomIdOrderByCheckInDateAsc(roomId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingDto createBooking(BookingDto.CreateRequest request) {
        log.info("Creating new booking for user id: {} and room id: {}",
                request.getUserId(), request.getRoomId());

        validateBookingDates(request.getCheckInDate(), request.getCheckOutDate());

        RoomDto room;
        try {
            room = roomServiceClient.getRoomById(request.getRoomId());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Room", "id", request.getRoomId());
        }

        if (!AVAILABLE.equals(room.getStatus())) {
            throw new BusinessException("Room is not available for booking");
        }

        if (!bookingRepository.isRoomAvailable(
                request.getRoomId(),
                request.getCheckInDate(),
                request.getCheckOutDate())) {
            throw new BusinessException("Room is already booked for the selected dates");
        }

        if (request.getNumberOfGuests() > room.getMaxOccupancy()) {
            throw new BusinessException(String.format(
                    "Number of guests (%d) exceeds room's maximum occupancy (%d)",
                    request.getNumberOfGuests(), room.getMaxOccupancy()));
        }

        long numberOfNights = ChronoUnit.DAYS.between(
                request.getCheckInDate(),
                request.getCheckOutDate());
        BigDecimal totalPrice = room.getPricePerNight()
                .multiply(BigDecimal.valueOf(numberOfNights));

        String confirmationNumber = generateConfirmationNumber();

        Booking booking = Booking.builder()
                .userId(request.getUserId())
                .roomId(request.getRoomId())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .numberOfGuests(request.getNumberOfGuests())
                .totalPrice(totalPrice)
                .status(Booking.BookingStatus.PENDING)
                .specialRequests(request.getSpecialRequests())
                .confirmationNumber(confirmationNumber)
                .paymentStatus(Booking.PaymentStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        try {
            roomServiceClient.updateRoomStatus(room.getId(), Map.of("status", "RESERVED"));
        } catch (Exception e) {
            log.error("Failed to update room status", e);
        }

        log.info("Booking created successfully with id: {} and confirmation number: {}",
                savedBooking.getId(), confirmationNumber);

        return convertToDto(savedBooking);
    }

    @Transactional
    public BookingDto updateBooking(Long id, BookingDto.UpdateRequest request) {
        log.info("Updating booking with id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        if (booking.getStatus() == Booking.BookingStatus.CANCELLED ||
                booking.getStatus() == Booking.BookingStatus.CHECKED_OUT) {
            throw new BusinessException("Cannot update a cancelled or checked-out booking");
        }

        if (request.getCheckInDate() != null || request.getCheckOutDate() != null) {
            LocalDate newCheckIn = request.getCheckInDate() != null ? request.getCheckInDate()
                    : booking.getCheckInDate();
            LocalDate newCheckOut = request.getCheckOutDate() != null ? request.getCheckOutDate()
                    : booking.getCheckOutDate();

            validateBookingDates(newCheckIn, newCheckOut);

            List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                    booking.getRoomId(), newCheckIn, newCheckOut);
            boolean hasConflict = overlappingBookings.stream()
                    .anyMatch(b -> !b.getId().equals(booking.getId()));

            if (hasConflict) {
                throw new BusinessException("Room is not available for the new dates");
            }

            booking.setCheckInDate(newCheckIn);
            booking.setCheckOutDate(newCheckOut);

            RoomDto room = roomServiceClient.getRoomById(booking.getRoomId());
            long numberOfNights = ChronoUnit.DAYS.between(newCheckIn, newCheckOut);
            BigDecimal totalPrice = room.getPricePerNight()
                    .multiply(BigDecimal.valueOf(numberOfNights));
            booking.setTotalPrice(totalPrice);
        }

        if (request.getNumberOfGuests() != null) {
            booking.setNumberOfGuests(request.getNumberOfGuests());
        }
        if (request.getSpecialRequests() != null) {
            booking.setSpecialRequests(request.getSpecialRequests());
        }
        if (request.getStatus() != null) {
            booking.setStatus(Booking.BookingStatus.valueOf(request.getStatus()));
        }
        if (request.getPaymentStatus() != null) {
            booking.setPaymentStatus(Booking.PaymentStatus.valueOf(request.getPaymentStatus()));
        }
        if (request.getPaymentMethod() != null) {
            booking.setPaymentMethod(request.getPaymentMethod());
        }
        if (request.getPaidAmount() != null) {
            booking.setPaidAmount(request.getPaidAmount());
        }

        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking updated successfully with id: {}", updatedBooking.getId());
        return convertToDto(updatedBooking);
    }

    @Transactional
    public BookingDto cancelBooking(Long id, String cancellationReason) {
        log.info("Cancelling booking with id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        if (!booking.canBeCancelled()) {
            throw new BusinessException("Booking cannot be cancelled in its current status");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setCancellationReason(cancellationReason);

        Booking cancelledBooking = bookingRepository.save(booking);

        try {
            roomServiceClient.updateRoomStatus(booking.getRoomId(), Map.of("status", AVAILABLE));
        } catch (Exception e) {
            log.error("Failed to update room status", e);
        }

        log.info("Booking cancelled successfully with id: {}", id);
        return convertToDto(cancelledBooking);
    }

    @Transactional
    public BookingDto confirmBooking(Long id, BookingDto.ConfirmRequest request) {
        log.info("Confirming booking with id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new BusinessException("Only pending bookings can be confirmed");
        }

        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setPaymentStatus(Booking.PaymentStatus.PAID);
        booking.setPaymentMethod(request.getPaymentMethod());
        booking.setPaidAmount(request.getPaymentAmount());

        Booking confirmedBooking = bookingRepository.save(booking);

        try {
            PaymentDto paymentDto = PaymentDto.builder()
                    .bookingId(confirmedBooking.getId())
                    .amount(request.getPaymentAmount())
                    .status("COMPLETED")
                    .paymentMethod(request.getPaymentMethod())
                    .paymentGateway("STRIPE")
                    .description("Payment for booking " + confirmedBooking.getConfirmationNumber())
                    .paymentDate(LocalDateTime.now())
                    .build();
            
            PaymentDto createdPayment = paymentServiceClient.createPayment(paymentDto);
            log.info("Payment created successfully with id: {} for booking: {}", 
                    createdPayment.getId(), confirmedBooking.getId());
        } catch (Exception e) {
            log.error("Failed to create payment record for booking: {}", confirmedBooking.getId(), e);
        }

        log.info("Booking confirmed successfully with id: {}", id);
        return convertToDto(confirmedBooking);
    }

    @Transactional
    public BookingDto checkInBooking(Long id) {
        log.info("Checking in booking with id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        if (booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
            throw new BusinessException("Only confirmed bookings can be checked in");
        }

        if (booking.getCheckInDate().isAfter(LocalDate.now())) {
            throw new BusinessException("Cannot check in before the check-in date");
        }

        booking.setStatus(Booking.BookingStatus.CHECKED_IN);
        
        try {
            roomServiceClient.updateRoomStatus(booking.getRoomId(), Map.of("status", "OCCUPIED"));
        } catch (Exception e) {
            log.error("Failed to update room status", e);
        }

        Booking checkedInBooking = bookingRepository.save(booking);
        log.info("Booking checked in successfully with id: {}", id);
        return convertToDto(checkedInBooking);
    }

    @Transactional
    public BookingDto checkOutBooking(Long id) {
        log.info("Checking out booking with id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        if (booking.getStatus() != Booking.BookingStatus.CHECKED_IN) {
            throw new BusinessException("Only checked-in bookings can be checked out");
        }

        booking.setStatus(Booking.BookingStatus.CHECKED_OUT);

        try {
            roomServiceClient.updateRoomStatus(booking.getRoomId(), Map.of("status", AVAILABLE));
        } catch (Exception e) {
            log.error("Failed to update room status", e);
        }

        Booking checkedOutBooking = bookingRepository.save(booking);
        log.info("Booking checked out successfully with id: {}", id);
        return convertToDto(checkedOutBooking);
    }

    @Transactional
    public void deleteBooking(Long id) {
        log.info("Deleting booking with id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        if (booking.isActive()) {
            throw new BusinessException("Cannot delete an active booking. Please cancel it first.");
        }

        bookingRepository.delete(booking);
        log.info("Booking deleted successfully with id: {}", id);
    }

    private void validateBookingDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn.isBefore(LocalDate.now())) {
            throw new BusinessException("Check-in date cannot be in the past");
        }
        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            throw new BusinessException("Check-out date must be after check-in date");
        }
        long numberOfNights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (numberOfNights > 30) {
            throw new BusinessException("Booking duration cannot exceed 30 nights");
        }
    }

    private String generateConfirmationNumber() {
        return "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private BookingDto convertToDto(Booking booking) {
        // Fetch room details for enrichment
        String roomNumber = "Unknown";

        try {
            RoomDto room = roomServiceClient.getRoomById(booking.getRoomId());
            roomNumber = room.getRoomNumber();
        } catch (Exception e) {
            log.warn("Failed to fetch room details for booking: {}", booking.getId());
        }

        return BookingDto.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .roomId(booking.getRoomId())
                .roomNumber(roomNumber)
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .numberOfGuests(booking.getNumberOfGuests())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus().name())
                .specialRequests(booking.getSpecialRequests())
                .confirmationNumber(booking.getConfirmationNumber())
                .paymentStatus(booking.getPaymentStatus().name())
                .paymentMethod(booking.getPaymentMethod())
                .paidAmount(booking.getPaidAmount())
                .numberOfNights(booking.getNumberOfNights())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}

