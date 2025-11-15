package com.sasps.hotelbooking.service;

import com.sasps.hotelbooking.dto.BookingDto;
import com.sasps.hotelbooking.exception.BusinessException;
import com.sasps.hotelbooking.exception.ResourceNotFoundException;
import com.sasps.hotelbooking.model.Booking;
import com.sasps.hotelbooking.model.Room;
import com.sasps.hotelbooking.model.User;
import com.sasps.hotelbooking.repository.BookingRepository;
import com.sasps.hotelbooking.repository.RoomRepository;
import com.sasps.hotelbooking.repository.UserRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    private final Counter bookingsCreatedCounter;
    private final Counter bookingsConfirmedCounter;
    private final Counter bookingsCancelledCounter;
    private final Counter bookingsCheckedInCounter;
    private final Counter bookingsCheckedOutCounter;
    private final Timer bookingCreationTimer;
    private final Counter totalRevenueCounter;

    public BookingService(BookingRepository bookingRepository,
            RoomRepository roomRepository,
            UserRepository userRepository,
            MeterRegistry meterRegistry) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;

        this.bookingsCreatedCounter = Counter.builder("bookings.created")
                .description("Total number of bookings created")
                .tag("architecture", "monolithic")
                .register(meterRegistry);

        this.bookingsConfirmedCounter = Counter.builder("bookings.confirmed")
                .description("Total number of bookings confirmed")
                .tag("architecture", "monolithic")
                .register(meterRegistry);

        this.bookingsCancelledCounter = Counter.builder("bookings.cancelled")
                .description("Total number of bookings cancelled")
                .tag("architecture", "monolithic")
                .register(meterRegistry);

        this.bookingsCheckedInCounter = Counter.builder("bookings.checkedin")
                .description("Total number of check-ins")
                .tag("architecture", "monolithic")
                .register(meterRegistry);

        this.bookingsCheckedOutCounter = Counter.builder("bookings.checkedout")
                .description("Total number of check-outs")
                .tag("architecture", "monolithic")
                .register(meterRegistry);

        this.bookingCreationTimer = Timer.builder("booking.creation.time")
                .description("Time taken to create a booking")
                .tag("architecture", "monolithic")
                .register(meterRegistry);

        this.totalRevenueCounter = Counter.builder("revenue.total")
                .description("Total revenue from confirmed bookings")
                .baseUnit("RON")
                .tag("architecture", "monolithic")
                .register(meterRegistry);
    }

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
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getUserActiveBookings(Long userId) {
        log.debug("Fetching active bookings for user id: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return bookingRepository.findActiveBookingsByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getRoomBookings(Long roomId) {
        log.debug("Fetching bookings for room id: {}", roomId);
        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Room", "id", roomId);
        }
        return bookingRepository.findByRoomIdOrderByCheckInDateAsc(roomId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingDto createBooking(BookingDto.CreateRequest request) {
        log.info("Creating new booking for user id: {} and room id: {}",
                request.getUserId(), request.getRoomId());

        Timer.Sample sample = Timer.start();

        try {
            validateBookingDates(request.getCheckInDate(), request.getCheckOutDate());
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));
            Room room = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room", "id", request.getRoomId()));
            if (room.getStatus() != Room.RoomStatus.AVAILABLE) {
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
                    .user(user)
                    .room(room)
                    .checkInDate(request.getCheckInDate())
                    .checkOutDate(request.getCheckOutDate())
                    .numberOfGuests(request.getNumberOfGuests())
                    .totalPrice(totalPrice)
                    .status(Booking.BookingStatus.CONFIRMED)
                    .specialRequests(request.getSpecialRequests())
                    .confirmationNumber(confirmationNumber)
                    .paymentStatus(Booking.PaymentStatus.PAID)
                    .paymentMethod(request.getPaymentMethod())
                    .paidAmount(totalPrice)
                    .build();
            Booking savedBooking = bookingRepository.save(booking);

            bookingsCreatedCounter.increment();
            totalRevenueCounter.increment(totalPrice.doubleValue());

            log.info("Booking created successfully with id: {} and confirmation number: {}",
                    savedBooking.getId(), confirmationNumber);

            return convertToDto(savedBooking);
        } finally {
            sample.stop(bookingCreationTimer);
        }
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
                    booking.getRoom().getId(), newCheckIn, newCheckOut);
            boolean hasConflict = overlappingBookings.stream()
                    .anyMatch(b -> !b.getId().equals(booking.getId()));
            if (hasConflict) {
                throw new BusinessException("Room is not available for the new dates");
            }
            booking.setCheckInDate(newCheckIn);
            booking.setCheckOutDate(newCheckOut);
            long numberOfNights = ChronoUnit.DAYS.between(newCheckIn, newCheckOut);
            BigDecimal totalPrice = booking.getRoom().getPricePerNight()
                    .multiply(BigDecimal.valueOf(numberOfNights));
            booking.setTotalPrice(totalPrice);
        }
        if (request.getNumberOfGuests() != null) {
            if (request.getNumberOfGuests() > booking.getRoom().getMaxOccupancy()) {
                throw new BusinessException("Number of guests exceeds room's maximum occupancy");
            }
            booking.setNumberOfGuests(request.getNumberOfGuests());
        }
        if (request.getSpecialRequests() != null) {
            booking.setSpecialRequests(request.getSpecialRequests());
        }
        if (request.getStatus() != null) {
            booking.setStatus(request.getStatus());
        }
        if (request.getPaymentStatus() != null) {
            booking.setPaymentStatus(request.getPaymentStatus());
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
    public BookingDto cancelBooking(Long id, BookingDto.CancelRequest request) {
        log.info("Cancelling booking with id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        if (!booking.canBeCancelled()) {
            throw new BusinessException("Booking cannot be cancelled in its current status");
        }
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setCancellationReason(request.getCancellationReason());
        Booking cancelledBooking = bookingRepository.save(booking);

        bookingsCancelledCounter.increment();

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

        bookingsConfirmedCounter.increment();
        totalRevenueCounter.increment(request.getPaymentAmount().doubleValue());

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
        booking.getRoom().setStatus(Room.RoomStatus.OCCUPIED);
        Booking checkedInBooking = bookingRepository.save(booking);

        bookingsCheckedInCounter.increment();

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
        booking.getRoom().setStatus(Room.RoomStatus.AVAILABLE);
        Booking checkedOutBooking = bookingRepository.save(booking);

        bookingsCheckedOutCounter.increment();

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

    public List<BookingDto> getUpcomingBookings() {
        log.debug("Fetching upcoming bookings");
        return bookingRepository.findUpcomingBookings(LocalDate.now()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getTodayCheckIns() {
        log.debug("Fetching today's check-ins");
        return bookingRepository.findBookingsForCheckInToday(LocalDate.now()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getTodayCheckOuts() {
        log.debug("Fetching today's check-outs");
        return bookingRepository.findBookingsForCheckOutToday(LocalDate.now()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
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
        return BookingDto.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .userFullName(booking.getUser().getFirstName() + " " + booking.getUser().getLastName())
                .roomId(booking.getRoom().getId())
                .roomNumber(booking.getRoom().getRoomNumber())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .numberOfGuests(booking.getNumberOfGuests())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .specialRequests(booking.getSpecialRequests())
                .confirmationNumber(booking.getConfirmationNumber())
                .paymentStatus(booking.getPaymentStatus())
                .paymentMethod(booking.getPaymentMethod())
                .paidAmount(booking.getPaidAmount())
                .numberOfNights(booking.getNumberOfNights())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}
