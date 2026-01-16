package com.foodroute.payment.service.controller;

import com.foodroute.payment.service.constant.PaymentStatus;
import com.foodroute.payment.service.dto.PaymentRequest;
import com.foodroute.payment.service.dto.PaymentResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @PostMapping
    public PaymentResponse makePayment(@RequestBody PaymentRequest paymentRequest){
        return new PaymentResponse(UUID.randomUUID().toString(), paymentRequest.orderId(), PaymentStatus.SUCCESS);
    }
}
