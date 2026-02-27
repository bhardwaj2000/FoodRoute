package com.foodroute.payment.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodroute.payment.service.constant.PaymentStatus;
import com.foodroute.payment.service.entity.Payment;
import com.foodroute.payment.service.event.OrderCancelledEvent;
import com.foodroute.payment.service.event.OrderCreatedEvent;
import com.foodroute.payment.service.event.PaymentCompletedEvent;
import com.foodroute.payment.service.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class PaymentEventListener {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public PaymentEventListener(PaymentRepository paymentRepository,
                                KafkaTemplate<String, Object> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "order-events", groupId = "foodroute-payment-group")
    public void handleOrderCreated(String inputOrderCreatedEvent) throws JsonProcessingException {

        log.info("Received OrderCreatedEvent: {}", inputOrderCreatedEvent);
        OrderCreatedEvent event = objectMapper.readValue(inputOrderCreatedEvent, OrderCreatedEvent.class);

        if (paymentRepository.existsByOrderId(event.orderId())) {
            log.info("Payment already processed for order: {}", event.orderId());
            return;
        }

        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setOrderId(event.orderId());
        payment.setAmount(event.amount());

        boolean success = ThreadLocalRandom.current().nextBoolean();
        String status = success ? PaymentStatus.SUCCESS.name() : PaymentStatus.FAILED.name();
        payment.setStatus(PaymentStatus.SUCCESS);

        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        log.info("Payment saved in db: {}", payment);

        PaymentCompletedEvent completed =
                new PaymentCompletedEvent(
                        event.orderId(),
                        payment.getPaymentId(),
                        status
                );

        kafkaTemplate.send("payment-events", completed);
        log.info("Publish PaymentCompletedEvent: {}", completed);
    }

    @KafkaListener(topics = "order-cancelled-events")
    public void handleOrderCancelled(String inputOrderCancelledEvent) throws JsonProcessingException {
        log.info("Received OrderCancelledEvent: {}", inputOrderCancelledEvent);

        OrderCancelledEvent event = objectMapper.readValue(inputOrderCancelledEvent, OrderCancelledEvent.class);

        Payment payment = paymentRepository
                .findByOrderId(event.orderId())
                .orElse(null);
        log.info("Payment found for order: {}", payment.getOrderId());
        if (payment == null) {
            return;
        }

        if ("SUCCESS".equals(payment.getStatus().name())) {

            payment.setStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(payment);

            System.out.println("Refund processed for order: " + event.orderId());
        }
    }
}
