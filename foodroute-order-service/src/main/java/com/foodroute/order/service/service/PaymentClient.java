package com.foodroute.order.service.service;

import com.foodroute.order.service.constant.PaymentStatus;
import com.foodroute.order.service.dto.request.PaymentRequest;
import com.foodroute.order.service.dto.response.PaymentResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

@Slf4j
@Service
public class PaymentClient {

    private final WebClient webClient;

    public PaymentClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @CircuitBreaker(name="paymentService", fallbackMethod = "paymentFallback")
    @Retry(name = "paymentService")
    public PaymentResponse initiatePayment(String orderId, BigDecimal amount){
        log.info("Initiating Payment service call");
        return webClient.post()
                .uri("http://foodroute-payment-service/payments")
                .bodyValue(new PaymentRequest(orderId,amount))
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .block();
    }

    private PaymentResponse paymentFallback(
            String orderId,
            BigDecimal amount,
            Throwable ex
    ) {
        return new PaymentResponse(null, orderId, PaymentStatus.FAILED);
    }
}
