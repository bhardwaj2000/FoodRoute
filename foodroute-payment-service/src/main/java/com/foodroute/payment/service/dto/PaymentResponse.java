package com.foodroute.payment.service.dto;

import com.foodroute.payment.service.constant.PaymentStatus;

import java.util.UUID;

public record PaymentResponse (
        String paymentId,
        String orderId,
        PaymentStatus status
) { }
