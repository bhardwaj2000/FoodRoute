package com.foodroute.order.service.service;

import com.foodroute.order.service.constant.OrderStatus;
import com.foodroute.order.service.entity.Order;
import com.foodroute.order.service.event.PaymentCompletedEvent;
import com.foodroute.order.service.repo.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentEventListener {

    private final OrderRepository orderRepository;

    public PaymentEventListener(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = "payment-events")
    public void handlePaymentCompleted(PaymentCompletedEvent event) {

        log.info("Received PaymentCompletedEvent: {}", event);

        Order order = orderRepository
                .findById(event.orderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("SUCCESS".equals(event.status())) {
            order.setStatus(OrderStatus.PAID);
        } else {
            order.setStatus(OrderStatus.CANCELLED);
        }

        orderRepository.save(order);
        log.info("Order updated in db: {}", order);
    }
}
