package com.deliveryplatform.trips.exceptions;

public class UnauthorizedTripActionException extends RuntimeException {
    public UnauthorizedTripActionException() {
        super("Not allowed to perform this action");
    }
}
