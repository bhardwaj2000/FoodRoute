package com.foodroute.order.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodroute.order.service.constant.OrderStatus;
import com.foodroute.order.service.dto.request.CreateOrderRequest;
import com.foodroute.order.service.dto.response.OrderResponse;
import com.foodroute.order.service.dto.response.PaymentResponse;
import com.foodroute.order.service.entity.Order;
import com.foodroute.order.service.entity.OutboxEvent;
import com.foodroute.order.service.event.OrderCreatedEvent;
import com.foodroute.order.service.repo.OrderRepository;
import com.foodroute.order.service.repo.OutboxRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService{

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;
    private final OutboxRepository outboxRepository;

    public OrderServiceImpl(KafkaTemplate<String, Object> kafkaTemplate, OrderRepository orderRepository, PaymentClient paymentClient, OutboxRepository outboxRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderRepository = orderRepository;
        this.paymentClient = paymentClient;
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
        order.setTotalAmount(BigDecimal.valueOf(500));

        order = orderRepository.save(order);

        // STEP 2: Move to PAYMENT_IN_PROGRESS
//        order.setStatus(OrderStatus.PAYMENT_IN_PROGRESS);
//        orderRepository.save(order);
        // ---- DB TRANSACTION ENDS HERE ----

        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getOrderId(),
                order.getUserId(),
                order.getRestaurantId(),
                order.getTotalAmount()
        );

        OutboxEvent outbox = new OutboxEvent();
        outbox.setAggregateId(order.getOrderId());
        outbox.setEventType("OrderCreatedEvent");
        ObjectMapper objectMapper = new ObjectMapper();
        outbox.setPayload(objectMapper.writeValueAsString(event));
        outbox.setProcessed(false);
        outbox.setCreatedAt(LocalDateTime.now());

        outboxRepository.save(outbox);

//        kafkaTemplate.send("order-events", order.getOrderId(), event);
//        log.info("Publish OrderCreatedEvent: {}", event);

        // STEP 3: Call Payment Service (outside transaction)
        /*try {
            PaymentResponse paymentResponse = paymentClient.initiatePayment(order.getOrderId(), order.getTotalAmount());

            if("SUCCESS".equals(paymentResponse.status().name())){
                order.setStatus(OrderStatus.PAID);
            } else {
                order.setStatus(OrderStatus.CANCELLED);
            }
        } catch (Exception e) {
            // STEP 4: Compensation
            order.setStatus(OrderStatus.CANCELLED);
        }*/


        // step 5: final update
//        orderRepository.save(order);
        return new OrderResponse(
                order.getOrderId(),
                order.getStatus(),
                order.getTotalAmount()
        );
    }

    @Override
    public OrderResponse getOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        return new OrderResponse(order.getOrderId(),order.getStatus(),order.getTotalAmount());
    }

}
