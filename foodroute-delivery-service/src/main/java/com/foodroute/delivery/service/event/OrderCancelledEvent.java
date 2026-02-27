package com.foodroute.delivery.service.event;

import java.time.LocalDateTime;

public record OrderCancelledEvent(
        String orderId,
        String reason,
        LocalDateTime cancelledAt
) {}
