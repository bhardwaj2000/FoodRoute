package com.foodroute.order.service.exception;

public class CancellationExpired extends RuntimeException {
    public CancellationExpired(String message) {
        super(message);
    }
}
