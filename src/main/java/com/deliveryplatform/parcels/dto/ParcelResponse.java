package com.deliveryplatform.parcels.dto;

import com.deliveryplatform.common.addresses.GeoAddress;
import com.deliveryplatform.parcels.ParcelSize;
import com.deliveryplatform.parcels.ParcelStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ParcelResponse(
        UUID id,
        UUID            userId,
        String          description,
        BigDecimal weightKg,
        ParcelSize size,
        boolean         fragile,
        GeoAddress pickupAddress,
        GeoAddress      dropoffAddress,
        ParcelStatus status,
        LocalDate deadlineDate,
        OffsetDateTime createdAt
) {}
