package com.deliveryplatform.trips.exceptions;

public class InvalidStopOrderException extends RuntimeException {
    public InvalidStopOrderException() {
        super("Stop order must be sequential starting from 1");
    }
}
