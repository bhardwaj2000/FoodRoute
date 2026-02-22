package com.foodroute.payment.service.service;

import com.foodroute.payment.service.constant.PaymentStatus;
import com.foodroute.payment.service.entity.Payment;
import com.foodroute.payment.service.event.OrderCreatedEvent;
import com.foodroute.payment.service.event.PaymentCompletedEvent;
import com.foodroute.payment.service.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class PaymentEventListener {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentEventListener(PaymentRepository paymentRepository,
                                KafkaTemplate<String, Object> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "order-events", groupId = "foodroute-payment-group")
    public void handleOrderCreated(OrderCreatedEvent event) {

        log.info("Received OrderCreatedEvent: {}", event);

        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setOrderId(event.orderId());
        payment.setAmount(event.amount());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        log.info("Payment saved in db: {}", payment);

        PaymentCompletedEvent completed =
                new PaymentCompletedEvent(
                        event.orderId(),
                        payment.getPaymentId(),
                        "SUCCESS"
                );

        kafkaTemplate.send("payment-events", completed);
        log.info("Publish PaymentCompletedEvent: {}", completed);
    }
}
