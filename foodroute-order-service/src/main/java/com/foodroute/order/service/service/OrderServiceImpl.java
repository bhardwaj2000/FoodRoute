package com.foodroute.order.service.service;

import com.foodroute.order.service.constant.OrderStatus;
import com.foodroute.order.service.dto.request.CreateOrderRequest;
import com.foodroute.order.service.dto.response.OrderResponse;
import com.foodroute.order.service.dto.response.PaymentResponse;
import com.foodroute.order.service.entity.Order;
import com.foodroute.order.service.repo.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class OrderServiceImpl implements OrderService{


    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;

    public OrderServiceImpl(OrderRepository orderRepository, PaymentClient paymentClient) {
        this.orderRepository = orderRepository;
        this.paymentClient = paymentClient;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        // STEP 1: Create Order (local transaction)
        Order order = new Order();
        order.setUserId(request.userId());
        order.setRestaurantId(request.restaurantId());
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalAmount(BigDecimal.valueOf(500));

        order = orderRepository.save(order);

        // STEP 2: Move to PAYMENT_IN_PROGRESS
        order.setStatus(OrderStatus.PAYMENT_IN_PROGRESS);
        orderRepository.save(order);
        // ---- DB TRANSACTION ENDS HERE ----

        // STEP 3: Call Payment Service (outside transaction)
        try {
            PaymentResponse paymentResponse = paymentClient.initiatePayment(order.getOrderId(), order.getTotalAmount());

            if("SUCCESS".equals(paymentResponse.status().name())){
                order.setStatus(OrderStatus.PAID);
            } else {
                order.setStatus(OrderStatus.CANCELLED);
            }
        } catch (Exception e) {
            // STEP 4: Compensation
            order.setStatus(OrderStatus.CANCELLED);
        }

        // step 5: final update
        orderRepository.save(order);
        return new OrderResponse(
                order.getOrderId(),
                order.getStatus(),
                order.getTotalAmount()
        );
    }

    @Override
    public OrderResponse getOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        return new OrderResponse(order.getOrderId(),order.getStatus(),order.getTotalAmount());
    }

}
