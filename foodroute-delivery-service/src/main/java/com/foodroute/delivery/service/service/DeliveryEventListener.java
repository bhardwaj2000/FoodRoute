package com.foodroute.delivery.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodroute.delivery.service.entity.Delivery;
import com.foodroute.delivery.service.event.OrderCancelledEvent;
import com.foodroute.delivery.service.event.PaymentCompletedEvent;
import com.foodroute.delivery.service.repo.DeliveryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class DeliveryEventListener {

    private final DeliveryRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public DeliveryEventListener(DeliveryRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "payment-events")
    public void handlePaymentCompleted(String inputPaymentEvent) throws JsonProcessingException {

        log.info("Received PaymentCompletedEvent: {}", inputPaymentEvent);
        PaymentCompletedEvent event =
                objectMapper.readValue(inputPaymentEvent, PaymentCompletedEvent.class);

        if (repository.existsByOrderId(event.orderId())) {
            log.info("Delivery already handled for order: {}", event.orderId());
            return;
        }
        Delivery delivery = new Delivery();
        delivery.setOrderId(event.orderId());
        delivery.setRiderName("RIDER-" + ThreadLocalRandom.current().nextInt(100));
        if("SUCCESS".equals(event.status())){
            delivery.setStatus("ASSIGNED");
        } else if ("FAILED".equals(event.status())){
            delivery.setStatus("CANCELLED");
        }
        delivery.setAssignedAt(LocalDateTime.now());

        repository.save(delivery);

        log.info("Delivery assigned for order: {}", event.orderId());
    }

    @KafkaListener(topics = "order-cancelled-events")
    public void handleOrderCancelled(String inputOrderCancelledEvent) throws JsonProcessingException {

        OrderCancelledEvent event = objectMapper.readValue(inputOrderCancelledEvent, OrderCancelledEvent.class);

        Delivery delivery = repository
                .findByOrderId(event.orderId())
                .orElse(null);

        if (delivery == null) {
            return;
        }

        delivery.setStatus("CANCELLED");
        repository.save(delivery);

        System.out.println("Delivery cancelled for order: " + event.orderId());
    }
}
