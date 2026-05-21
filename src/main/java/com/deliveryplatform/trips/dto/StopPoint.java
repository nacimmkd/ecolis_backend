package com.deliveryplatform.trips.dto;

import com.deliveryplatform.addresses.GeocodedAddress;
import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record StopPoint(
        UUID stopId,
        Integer stopOrder,
        GeocodedAddress address
) {}