package com.deliveryplatform.parcels;

public class ParcelNotFoundException extends RuntimeException {
    public ParcelNotFoundException() {
        super("Parcel Not Found");
    }
}
