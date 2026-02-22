package com.foodroute.delivery.service.event;

public record PaymentCompletedEvent(
        String orderId,
        String paymentId,
        String status
) {}
