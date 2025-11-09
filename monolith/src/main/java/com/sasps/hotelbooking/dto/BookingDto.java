package com.sasps.hotelbooking.dto;

import com.sasps.hotelbooking.model.Booking;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {
    private Long id;
    private Long userId;
    private String userFullName;
    private Long roomId;
    private String roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfGuests;
    private BigDecimal totalPrice;
    private Booking.BookingStatus status;
    private String specialRequests;
    private String confirmationNumber;
    private Booking.PaymentStatus paymentStatus;
    private String paymentMethod;
    private BigDecimal paidAmount;
    private Long numberOfNights;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        @NotNull(message = "User ID is required")
        private Long userId;

        @NotNull(message = "Room ID is required")
        private Long roomId;

        @NotNull(message = "Check-in date is required")
        @FutureOrPresent(message = "Check-in date must be today or in the future")
        private LocalDate checkInDate;

        @NotNull(message = "Check-out date is required")
        @Future(message = "Check-out date must be in the future")
        private LocalDate checkOutDate;

        @NotNull(message = "Number of guests is required")
        @Min(value = 1, message = "Number of guests must be at least 1")
        @Max(value = 10, message = "Number of guests must not exceed 10")
        private Integer numberOfGuests;

        @Size(max = 1000, message = "Special requests must not exceed 1000 characters")
        private String specialRequests;

        @Size(max = 50, message = "Payment method must not exceed 50 characters")
        private String paymentMethod;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        @FutureOrPresent(message = "Check-in date must be today or in the future")
        private LocalDate checkInDate;

        @Future(message = "Check-out date must be in the future")
        private LocalDate checkOutDate;

        @Min(value = 1, message = "Number of guests must be at least 1")
        @Max(value = 10, message = "Number of guests must not exceed 10")
        private Integer numberOfGuests;

        @Size(max = 1000, message = "Special requests must not exceed 1000 characters")
        private String specialRequests;

        private Booking.BookingStatus status;
        private Booking.PaymentStatus paymentStatus;

        @Size(max = 50, message = "Payment method must not exceed 50 characters")
        private String paymentMethod;

        @DecimalMin(value = "0.0", message = "Paid amount must be non-negative")
        private BigDecimal paidAmount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CancelRequest {
        @NotBlank(message = "Cancellation reason is required")
        @Size(max = 500, message = "Cancellation reason must not exceed 500 characters")
        private String cancellationReason;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ConfirmRequest {
        @NotNull(message = "Payment amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Payment amount must be greater than 0")
        private BigDecimal paymentAmount;

        @NotBlank(message = "Payment method is required")
        private String paymentMethod;
    }
}
