package com.foodroute.order.service.exception;

public final class OrderNotFoundError implements OrderError {

    public String message() {
        return "Order not found";
    }
}
