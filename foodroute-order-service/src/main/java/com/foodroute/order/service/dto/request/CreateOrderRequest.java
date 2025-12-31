package com.foodroute.order.service.dto.request;

import java.util.List;

public record CreateOrderRequest(
        String userId,
        String restaurantId,
        List<OrderItemRequest> items
) {}
