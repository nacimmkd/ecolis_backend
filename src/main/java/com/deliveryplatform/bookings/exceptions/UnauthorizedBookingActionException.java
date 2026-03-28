package com.deliveryplatform.bookings.exceptions;

public class UnauthorizedBookingActionException extends RuntimeException {
    public UnauthorizedBookingActionException(String message) {
        super(message);
    }
}
