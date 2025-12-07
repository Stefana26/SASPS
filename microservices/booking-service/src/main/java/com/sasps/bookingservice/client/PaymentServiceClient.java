package com.sasps.bookingservice.client;

import com.sasps.bookingservice.dto.PaymentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "http://payment-service:8084")
public interface PaymentServiceClient {
    
    @PostMapping("/api/payments")
    PaymentDto createPayment(@RequestBody PaymentDto paymentDto);
}
