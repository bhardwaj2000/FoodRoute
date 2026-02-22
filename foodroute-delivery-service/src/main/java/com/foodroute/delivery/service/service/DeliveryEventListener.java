package com.foodroute.delivery.service.service;

import com.foodroute.delivery.service.entity.Delivery;
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

    public DeliveryEventListener(DeliveryRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "payment-events")
    public void handlePaymentCompleted(PaymentCompletedEvent event) {

        if (!"SUCCESS".equals(event.status())) {
            return;
        }

        Delivery delivery = new Delivery();
        delivery.setOrderId(event.orderId());
        delivery.setRiderName("RIDER-" + ThreadLocalRandom.current().nextInt(100));
        delivery.setStatus("ASSIGNED");
        delivery.setAssignedAt(LocalDateTime.now());

        repository.save(delivery);

        log.info("Delivery assigned for order: {}", event.orderId());
    }
}
