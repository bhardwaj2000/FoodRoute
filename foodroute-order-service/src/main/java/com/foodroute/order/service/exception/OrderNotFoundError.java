package com.foodroute.order.service.exception;

public class OrderNotFoundError extends RuntimeException{

    public OrderNotFoundError(String msg) {super(msg);}
}
