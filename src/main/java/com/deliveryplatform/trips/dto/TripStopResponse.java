package com.deliveryplatform.trips.dto;

import com.deliveryplatform.addresses.GeocodedAddress;

import java.util.UUID;

public record TripStopResponse(
        UUID tripStopId,
        Integer stopOrder,
        GeocodedAddress address
) {

    public TripStopResponse withAddress(GeocodedAddress address) {
        return new TripStopResponse(tripStopId, stopOrder, address);
    }
}