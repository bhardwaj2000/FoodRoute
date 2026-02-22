package com.foodroute.payment.service.dto;

import java.math.BigDecimal;

public record PaymentRequest(String orderId, BigDecimal amount) {
}
