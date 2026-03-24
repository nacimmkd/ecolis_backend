package com.deliveryplatform.trips.exceptions;

public class TripStopNotFound extends RuntimeException {
    public TripStopNotFound(String message) {
        super(message);
    }
}
