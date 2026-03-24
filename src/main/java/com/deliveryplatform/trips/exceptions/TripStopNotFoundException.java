package com.deliveryplatform.trips.exceptions;

public class TripStopNotFoundException extends RuntimeException {
    public TripStopNotFoundException() {
        super("Trip stop not found");
    }
}
