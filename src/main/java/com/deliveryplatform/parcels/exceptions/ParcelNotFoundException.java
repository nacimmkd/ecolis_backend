package com.deliveryplatform.parcels.exceptions;

public class ParcelNotFoundException extends RuntimeException {
    public ParcelNotFoundException() {
        super("Parcel Not Found");
    }
}
