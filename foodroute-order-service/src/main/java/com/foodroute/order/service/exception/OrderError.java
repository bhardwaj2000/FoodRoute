package com.foodroute.order.service.exception;

public sealed interface OrderError
        permits InvalidOrderError, OrderNotFoundError {
}

