package com.foodroute.order.service.event;

public record PaymentCompletedEvent(
        String orderId,
        String paymentId,
        String status
) { }
