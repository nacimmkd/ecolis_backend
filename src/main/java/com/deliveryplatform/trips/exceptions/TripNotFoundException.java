package com.deliveryplatform.trips.exceptions;

public class TripNotFoundException extends RuntimeException {
    public TripNotFoundException() {
        super("Trip not found");
    }
}
