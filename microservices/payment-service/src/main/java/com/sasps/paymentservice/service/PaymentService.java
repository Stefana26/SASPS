package com.sasps.paymentservice.service;

import com.sasps.paymentservice.model.Payment;
import com.sasps.paymentservice.repository.PaymentRepository;
import com.sasps.paymentservice.dto.PaymentDto;
import com.sasps.paymentservice.exception.BusinessException;
import com.sasps.paymentservice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public List<PaymentDto> getAllPayments() {
        log.debug("Fetching all payments");
        return paymentRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public PaymentDto getPaymentById(Long id) {
        log.debug("Fetching payment with id: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        return convertToDto(payment);
    }

    public PaymentDto getPaymentByBookingId(Long bookingId) {
        log.debug("Fetching payment for booking id: {}", bookingId);
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "bookingId", bookingId));
        return convertToDto(payment);
    }

    public PaymentDto getPaymentByTransactionId(String transactionId) {
        log.debug("Fetching payment with transaction id: {}", transactionId);
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "transactionId", transactionId));
        return convertToDto(payment);
    }

    @Transactional
    public PaymentDto createPayment(PaymentDto paymentDto) {
        log.info("Creating new payment for booking id: {}", paymentDto.getBookingId());

        if (paymentRepository.findByBookingId(paymentDto.getBookingId()).isPresent()) {
            throw new BusinessException("Payment already exists for booking: " + paymentDto.getBookingId());
        }

        String transactionId = generateTransactionId();

        Payment payment = Payment.builder()
                .bookingId(paymentDto.getBookingId())
                .amount(paymentDto.getAmount())
                .status(Payment.PaymentStatus.PROCESSING)
                .paymentMethod(Payment.PaymentMethod.valueOf(paymentDto.getPaymentMethod()))
                .transactionId(transactionId)
                .paymentGateway(paymentDto.getPaymentGateway())
                .description(paymentDto.getDescription())
                .build();

        boolean paymentSuccess = processPayment(payment);

        if (paymentSuccess) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setPaymentDate(LocalDateTime.now());
            log.info("Payment processed successfully");
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason("Payment gateway declined the transaction");
            log.warn("Payment processing failed");
        }

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created with id: {}", savedPayment.getId());

        return convertToDto(savedPayment);
    }

    @Transactional
    public PaymentDto refundPayment(Long id, String reason) {
        log.info("Refunding payment with id: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));

        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new BusinessException("Only completed payments can be refunded");
        }

        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        payment.setFailureReason(reason);

        Payment refundedPayment = paymentRepository.save(payment);
        log.info("Payment refunded successfully with id: {}", id);

        return convertToDto(refundedPayment);
    }

    @Transactional
    public void deletePayment(Long id) {
        log.info("Deleting payment with id: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        paymentRepository.delete(payment);
        log.info("Payment deleted successfully with id: {}", id);
    }

    private boolean processPayment(Payment payment) {
        // TODO: SIMULATE PAYMENT
        try {
            Thread.sleep(1000); // Simulate processing time
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().toUpperCase();
    }

    private PaymentDto convertToDto(Payment payment) {
        return PaymentDto.builder()
                .id(payment.getId())
                .bookingId(payment.getBookingId())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .paymentMethod(payment.getPaymentMethod().name())
                .transactionId(payment.getTransactionId())
                .paymentGateway(payment.getPaymentGateway())
                .paymentDate(payment.getPaymentDate())
                .description(payment.getDescription())
                .failureReason(payment.getFailureReason())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}

