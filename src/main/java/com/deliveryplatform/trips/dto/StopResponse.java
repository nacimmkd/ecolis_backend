package com.deliveryplatform.trips.dto;

import com.deliveryplatform.addresses.GeocodedAddress;

import java.util.UUID;

public record StopResponse(
        UUID id,
        Integer       stopOrder,
        GeocodedAddress address
) {}
