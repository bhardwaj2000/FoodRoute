package com.foodroute.order.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OrderExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(RuntimeException.class)
    public org.springframework.http.ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        if(ex instanceof OrderNotFoundError || ex instanceof CancellationExpired || ex instanceof InvalidOrderError) {
            return org.springframework.http.ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ex.getMessage());
        } else {
            return org.springframework.http.ResponseEntity
                    .status(org.springframework.http.HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());
        }
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(com.fasterxml.jackson.core.JsonProcessingException.class)
    public org.springframework.http.ResponseEntity<String> handleJsonException(com.fasterxml.jackson.core.JsonProcessingException ex) {
        return org.springframework.http.ResponseEntity
                .status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing event data : " + ex.getMessage());
    }

}
