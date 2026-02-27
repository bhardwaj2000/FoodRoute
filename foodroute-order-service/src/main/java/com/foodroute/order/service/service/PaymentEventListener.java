package com.foodroute.order.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodroute.order.service.constant.OrderStatus;
import com.foodroute.order.service.constant.PaymentStatus;
import com.foodroute.order.service.entity.Order;
import com.foodroute.order.service.event.PaymentCompletedEvent;
import com.foodroute.order.service.repo.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.foodroute.order.service.constant.OrderStatus.PAID;

@Slf4j
@Service
public class PaymentEventListener {

    private final OrderRepository orderRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    public PaymentEventListener(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = "payment-events")
    public void handlePaymentCompleted(String inputPaymentEvent) throws JsonProcessingException {

        log.info("Received PaymentCompletedEvent: {}", inputPaymentEvent);

        PaymentCompletedEvent event = objectMapper.readValue(inputPaymentEvent, PaymentCompletedEvent.class);
        Order order = orderRepository
                .findById(event.orderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == PAID && Objects.equals(event.status(), PaymentStatus.SUCCESS.name())) return;

        if ("SUCCESS".equals(event.status())) {
            order.setStatus(PAID);
        } else {
            order.setStatus(OrderStatus.CANCELLED);
        }

        orderRepository.save(order);
        log.info("Order updated in db: {}", order);
    }
}
