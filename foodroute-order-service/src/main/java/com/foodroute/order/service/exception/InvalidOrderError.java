package com.foodroute.order.service.exception;

public final class InvalidOrderError implements OrderError{

    public String message() {
        return "Order data is invalid";
    }
}
