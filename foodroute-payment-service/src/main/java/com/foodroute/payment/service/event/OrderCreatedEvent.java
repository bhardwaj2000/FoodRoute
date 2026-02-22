package com.foodroute.payment.service.event;

import java.math.BigDecimal;

public record OrderCreatedEvent(
        String orderId,
        String userId,
        String restaurantId,
        BigDecimal amount
) {}
