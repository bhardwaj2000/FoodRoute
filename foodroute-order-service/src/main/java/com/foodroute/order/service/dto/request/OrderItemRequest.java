package com.foodroute.order.service.dto.request;

public record OrderItemRequest(
        String itemId,
        int quantity,
        double price
) {}
