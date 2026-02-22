package com.foodroute.order.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodroute.order.service.entity.OutboxEvent;
import com.foodroute.order.service.event.OrderCreatedEvent;
import com.foodroute.order.service.repo.OutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class OutboxPublisher {

    private final OutboxRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public OutboxPublisher(OutboxRepository repository,
                           KafkaTemplate<String, Object> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() throws JsonProcessingException {

        List<OutboxEvent> events = repository.findByProcessedFalse();

        log.info("Order-event started publishing");
        for (OutboxEvent event : events) {
            OrderCreatedEvent eventObject =
                    objectMapper.readValue(
                            event.getPayload(),
                            OrderCreatedEvent.class
                    );
            kafkaTemplate.send("order-events",
                    event.getAggregateId(),
                    eventObject);

            event.setProcessed(true);
            repository.save(event);
            log.info("Order-event published: {}", event);
        }
    }
}
