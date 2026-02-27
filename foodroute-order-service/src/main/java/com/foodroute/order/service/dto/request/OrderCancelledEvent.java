package com.foodroute.order.service.dto.request;

public record OrderCancelledEvent(
        String orderId,
        String reason
) {}
