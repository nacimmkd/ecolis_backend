package com.deliveryplatform.parcels;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ParcelState {

    PUBLISHED("Parcel published"),
    BOOKED("Parcel booked"),
    PICKED_UP("Parcel picked up"),
    IN_TRANSIT("Parcel in transit"),
    DELIVERED("Parcel delivered"),
    CANCELLED("Parcel cancelled"),;

    private String message;

}
