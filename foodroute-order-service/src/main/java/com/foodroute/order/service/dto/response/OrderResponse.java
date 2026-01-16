package com.foodroute.order.service.dto.response;

import com.foodroute.order.service.constant.OrderStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderResponse(
        String orderId,
        OrderStatus status,
        BigDecimal totalAmount
) {}

