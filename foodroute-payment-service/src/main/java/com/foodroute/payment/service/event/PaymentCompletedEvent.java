package com.foodroute.payment.service.event;

public record PaymentCompletedEvent(
        String orderId,
        String paymentId,
        String status
) { }
