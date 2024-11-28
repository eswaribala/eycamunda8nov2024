package com.ey.orderapi.exceptions;

public class StockQtyNotAvailable extends RuntimeException{
    public StockQtyNotAvailable(String message) {
        super(message);
    }
}
