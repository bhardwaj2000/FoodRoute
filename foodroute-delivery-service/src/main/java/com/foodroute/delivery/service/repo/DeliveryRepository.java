package com.foodroute.delivery.service.repo;

import com.foodroute.delivery.service.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    boolean existsByOrderId(String orderId);

    Optional<Delivery> findByOrderId(String orderId);
}
