package com.foodroute.order.service.dto.response;

import com.foodroute.order.service.constant.PaymentStatus;

import java.util.UUID;

public record PaymentResponse(
        String paymentId,
        String orderId,
        PaymentStatus status
) { }
