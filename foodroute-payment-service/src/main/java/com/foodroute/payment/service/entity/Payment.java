package com.foodroute.payment.service.entity;

import com.foodroute.payment.service.constant.PaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @Column(length = 36)
    private String paymentId;

    @Column(nullable = false, length = 36)
    private String orderId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime createdAt;

    @PrePersist
    void generateId() {
        this.paymentId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }
}
