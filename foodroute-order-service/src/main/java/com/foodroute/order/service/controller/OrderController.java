package com.foodroute.order.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.foodroute.order.service.dto.request.CreateOrderRequest;
import com.foodroute.order.service.dto.response.OrderCancelResponse;
import com.foodroute.order.service.dto.response.OrderResponse;
import com.foodroute.order.service.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest request) throws JsonProcessingException {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    // SELECT BIN_TO_UUID(order_id) FROM orders;
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> get(@PathVariable String id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderCancelResponse> cancelOrder(@PathVariable String orderId) throws JsonProcessingException {
        OrderCancelResponse orderCancelResponse = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(orderCancelResponse);
    }
}
