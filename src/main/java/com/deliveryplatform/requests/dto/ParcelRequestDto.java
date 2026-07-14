package com.deliveryplatform.requests.dto;

import com.deliveryplatform.requests.RequestState;
import com.deliveryplatform.trips.dto.TripSummary;
import com.deliveryplatform.users.dto.UserBrief;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ParcelRequestDto(
        UUID requestId,
        TripSummary trip,
        UserBrief carrier,
        RequestState state,
        BigDecimal pickupDetourKm,
        BigDecimal dropOffDetourKm,
        String rejectionReason,
        OffsetDateTime requestedAt,
        OffsetDateTime respondedAt
) {
}
