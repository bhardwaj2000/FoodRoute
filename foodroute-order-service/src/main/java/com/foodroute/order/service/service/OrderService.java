package com.foodroute.order.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.foodroute.order.service.dto.request.CreateOrderRequest;
import com.foodroute.order.service.dto.response.OrderResponse;

import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request) throws JsonProcessingException;
    OrderResponse getOrder(String orderId);
}
