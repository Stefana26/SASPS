package com.sasps.paymentservice.controller;

import com.sasps.paymentservice.service.PaymentService;
import com.sasps.paymentservice.dto.PaymentDto;
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
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Management", description = "APIs for managing payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    @Operation(summary = "Get all payments", description = "Retrieve all payments")
    public ResponseEntity<List<PaymentDto>> getAllPayments() {
        log.info("GET /api/payments - Get all payments");
        List<PaymentDto> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieve a specific payment by its ID")
    public ResponseEntity<PaymentDto> getPaymentById(
            @Parameter(description = "Payment ID") @PathVariable Long id) {
        log.info("GET /api/payments/{} - Get payment by ID", id);
        PaymentDto payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/booking/{bookingId}")
    @Operation(summary = "Get payment by booking ID", description = "Retrieve payment for a specific booking")
    public ResponseEntity<PaymentDto> getPaymentByBookingId(
            @Parameter(description = "Booking ID") @PathVariable Long bookingId) {
        log.info("GET /api/payments/booking/{} - Get payment by booking ID", bookingId);
        PaymentDto payment = paymentService.getPaymentByBookingId(bookingId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Get payment by transaction ID", description = "Retrieve payment by transaction ID")
    public ResponseEntity<PaymentDto> getPaymentByTransactionId(
            @Parameter(description = "Transaction ID") @PathVariable String transactionId) {
        log.info("GET /api/payments/transaction/{} - Get payment by transaction ID", transactionId);
        PaymentDto payment = paymentService.getPaymentByTransactionId(transactionId);
        return ResponseEntity.ok(payment);
    }

    @PostMapping
    @Operation(summary = "Create payment", description = "Process a new payment")
    public ResponseEntity<PaymentDto> createPayment(@Valid @RequestBody PaymentDto paymentDto) {
        log.info("POST /api/payments - Create payment");
        PaymentDto createdPayment = paymentService.createPayment(paymentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
    }

    @PostMapping("/{id}/refund")
    @Operation(summary = "Refund payment", description = "Refund a completed payment")
    public ResponseEntity<PaymentDto> refundPayment(
            @Parameter(description = "Payment ID") @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        log.info("POST /api/payments/{}/refund - Refund payment", id);
        String reason = request.getOrDefault("reason", "Customer request");
        PaymentDto refundedPayment = paymentService.refundPayment(id, reason);
        return ResponseEntity.ok(refundedPayment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment", description = "Delete a payment record")
    public ResponseEntity<Void> deletePayment(
            @Parameter(description = "Payment ID") @PathVariable Long id) {
        log.info("DELETE /api/payments/{} - Delete payment", id);
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}

