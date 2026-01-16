package com.foodroute.order.service.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(String orderId, BigDecimal amount) {
}
