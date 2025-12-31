package com.foodroute.order.service.service;

import com.foodroute.order.service.constant.OrderStatus;
import com.foodroute.order.service.dto.request.CreateOrderRequest;
import com.foodroute.order.service.dto.response.OrderResponse;
import com.foodroute.order.service.entity.Order;
import com.foodroute.order.service.exception.OrderNotFoundError;
import com.foodroute.order.service.repo.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setUserId(request.userId());
        order.setRestaurantId(request.restaurantId());
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        // totalAmount calculation mocked for now
        order.setTotalAmount(BigDecimal.valueOf(500));

        Order saved = orderRepository.save(order);

        return new OrderResponse(
                saved.getOrderId(),
                saved.getStatus(),
                saved.getTotalAmount()
        );
    }

    @Override
    public OrderResponse getOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        return new OrderResponse(order.getOrderId(),order.getStatus(),order.getTotalAmount());
    }
}
