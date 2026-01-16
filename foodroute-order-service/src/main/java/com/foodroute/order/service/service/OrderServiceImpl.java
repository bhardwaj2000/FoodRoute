package com.foodroute.order.service.service;

import com.foodroute.order.service.constant.OrderStatus;
import com.foodroute.order.service.dto.request.CreateOrderRequest;
import com.foodroute.order.service.dto.request.PaymentRequest;
import com.foodroute.order.service.dto.response.OrderResponse;
import com.foodroute.order.service.dto.response.PaymentResponse;
import com.foodroute.order.service.entity.Order;
import com.foodroute.order.service.repo.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService{

    private final WebClient webClient;

    private final OrderRepository orderRepository;

    public OrderServiceImpl(WebClient webClient, OrderRepository orderRepository) {
        this.webClient = webClient;
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setUserId(request.userId());
        order.setRestaurantId(request.restaurantId());
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        // totalAmount calculation mocked for now
        order.setTotalAmount(BigDecimal.valueOf(500));

        Order saved = orderRepository.save(order);

        return new OrderResponse(
                saved.getOrderId(),
                saved.getStatus(),
                saved.getTotalAmount()
        );
    }

    @Override
    public OrderResponse getOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        return new OrderResponse(order.getOrderId(),order.getStatus(),order.getTotalAmount());
    }

    public PaymentResponse initiatePayment(String orderId, BigDecimal amount){
        return webClient.post()
                .uri("http://foodroute-payment-service/payments")
                .bodyValue(new PaymentRequest(orderId,amount))
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .block();
    }
}
