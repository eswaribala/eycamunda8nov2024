package com.ey.orderapi.exceptions;

public class OrderIdException extends RuntimeException{
    public OrderIdException(String message) {
        super(message);
    }
}
