package com.deliveryplatform.trips.dto;

import com.deliveryplatform.common.addresses.GeoAddress;

import java.util.UUID;

public record StopResponse(
        UUID id,
        Integer       stopOrder,
        GeoAddress address
) {}
