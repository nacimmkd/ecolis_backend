package com.deliveryplatform.trips.exceptions;

public class IllegalTripStateException extends RuntimeException {
    public IllegalTripStateException() {
        super("Cannot update a trip with this status");
    }
}
