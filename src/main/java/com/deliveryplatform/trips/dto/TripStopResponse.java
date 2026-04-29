package com.deliveryplatform.trips.dto;

import com.deliveryplatform.addresses.GeocodedAddress;
import com.deliveryplatform.trips.TripStop;

import java.util.UUID;

public record TripStopResponse(
        UUID         tripStopId,
        Integer       stopOrder,
        GeocodedAddress address
) {

    public static TripStopResponse of(TripStop tripStop) {
        return new TripStopResponse(
                tripStop.getId(),
                tripStop.getStopOrder(),
                tripStop.getAddress()
        );
    }
}
