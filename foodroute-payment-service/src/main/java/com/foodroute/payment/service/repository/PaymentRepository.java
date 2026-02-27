package com.foodroute.payment.service.repository;

import com.foodroute.payment.service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    boolean existsByOrderId(String s);

    Optional<Payment> findByOrderId(String orderId);
}
