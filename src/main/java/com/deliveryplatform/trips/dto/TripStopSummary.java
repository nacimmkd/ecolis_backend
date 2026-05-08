package com.deliveryplatform.trips.dto;

import com.deliveryplatform.addresses.GeocodedAddress;
import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record TripStopSummary(
        UUID tripStopId,
        Integer stopOrder,
        GeocodedAddress address
) {}