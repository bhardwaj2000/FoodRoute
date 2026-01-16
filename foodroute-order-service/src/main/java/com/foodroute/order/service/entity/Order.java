package com.foodroute.order.service.entity;

import com.foodroute.order.service.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Column(length = 36)
    private String orderId;

    private String userId;
    private String restaurantId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal totalAmount;

    private LocalDateTime createdAt;

    @PrePersist
    void generateId() {
        this.orderId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }
}
