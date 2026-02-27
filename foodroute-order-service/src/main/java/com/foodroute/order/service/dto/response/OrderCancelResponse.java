package com.foodroute.order.service.dto.response;

import com.foodroute.order.service.constant.OrderStatus;
import com.foodroute.order.service.constant.PaymentStatus;

import java.math.BigDecimal;

public record OrderCancelResponse(
        String orderId,
        OrderStatus status,
        PaymentStatus paymentStatus,
        BigDecimal totalAmount
) {}

