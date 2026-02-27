package com.foodroute.order.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodroute.order.service.constant.OrderStatus;
import com.foodroute.order.service.constant.PaymentStatus;
import com.foodroute.order.service.dto.request.CreateOrderRequest;
import com.foodroute.order.service.dto.request.OrderCancelledEvent;
import com.foodroute.order.service.dto.response.OrderCancelResponse;
import com.foodroute.order.service.dto.response.OrderResponse;
import com.foodroute.order.service.entity.Order;
import com.foodroute.order.service.entity.OutboxEvent;
import com.foodroute.order.service.event.OrderCreatedEvent;
import com.foodroute.order.service.exception.CancellationExpired;
import com.foodroute.order.service.exception.InvalidOrderError;
import com.foodroute.order.service.exception.OrderNotFoundError;
import com.foodroute.order.service.repo.OrderRepository;
import com.foodroute.order.service.repo.OutboxRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OutboxRepository outboxRepository) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) throws JsonProcessingException {
        // STEP 1: Create Order (local transaction)
        log.info("Order received: {}", request);
        Order order = new Order();
        order.setUserId(request.userId());
        order.setRestaurantId(request.restaurantId());
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());
        double totalAmount = request.items().stream().mapToDouble(item -> item.price() * item.quantity()).sum();
        order.setTotalAmount(BigDecimal.valueOf(totalAmount));

        order = orderRepository.save(order);
        log.info("order saved in orders with order id {}", order.getOrderId());
        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getOrderId(),
                order.getUserId(),
                order.getRestaurantId(),
                order.getTotalAmount()
        );

        saveOrderCreateOutbox(order, event);

        return new OrderResponse(
                order.getOrderId(),
                order.getStatus(),
                order.getTotalAmount()
        );
    }

    private void saveOrderCreateOutbox(Order order, OrderCreatedEvent event) throws JsonProcessingException {
        OutboxEvent outbox = new OutboxEvent();
        outbox.setAggregateId(order.getOrderId());
        outbox.setEventType("OrderCreatedEvent");
        ObjectMapper objectMapper = new ObjectMapper();
        outbox.setPayload(objectMapper.writeValueAsString(event));
        outbox.setProcessed(false);
        outbox.setCreatedAt(LocalDateTime.now());

        outboxRepository.save(outbox);
        log.info("order saved in OutboxEvent for order creation with aggregate id {}", outbox.getAggregateId());
    }

    @Override
    public OrderResponse getOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundError("Order not found for order id: " + orderId));
        return new OrderResponse(order.getOrderId(),order.getStatus(),order.getTotalAmount());
    }

    @Override
    @Transactional
    public OrderCancelResponse cancelOrder(String orderId) throws JsonProcessingException {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundError("Order not found for order id: " + orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderError("Order is already cancelled for order id: " + orderId);
        }

        long minutes = Duration.between(
                order.getCreatedAt(),
                LocalDateTime.now()
        ).toMinutes();

        if (minutes > 5) {
            throw new CancellationExpired("Cancellation window expired for order id: " + orderId + "with minutes > 1 : " + minutes);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        OrderCancelledEvent event = new OrderCancelledEvent(
                orderId,
                "User Cancelled"
        );

        saveOrderCancelOutbox(event);  // reuse outbox pattern

        return new OrderCancelResponse(
                order.getOrderId(),
                order.getStatus(),
                PaymentStatus.REFUNDED,
                order.getTotalAmount()
        );
    }

    private void saveOrderCancelOutbox(OrderCancelledEvent event) throws JsonProcessingException {
        OutboxEvent outbox = new OutboxEvent();
        outbox.setAggregateId(event.orderId());
        outbox.setEventType("OrderCancelEvent");
        ObjectMapper objectMapper = new ObjectMapper();
        outbox.setPayload(objectMapper.writeValueAsString(event));
        outbox.setProcessed(false);
        outbox.setCreatedAt(LocalDateTime.now());

        outboxRepository.save(outbox);
        log.info("order saved in OutboxEvent for user cancellation with aggregate id {}", outbox.getAggregateId());
    }

}
