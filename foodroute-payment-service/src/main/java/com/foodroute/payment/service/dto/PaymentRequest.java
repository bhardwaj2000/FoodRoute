package com.foodroute.payment.service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(String orderId, BigDecimal amount) {
}
