package com.deliveryplatform.requests.dto;

import com.deliveryplatform.parcels.dto.ParcelSummary;
import com.deliveryplatform.requests.RequestState;
import com.deliveryplatform.users.dto.UserBrief;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record TripRequestDto(
        UUID requestId,
        ParcelSummary parcel,
        UserBrief sender,
        RequestState state,
        BigDecimal pickupDetourKm,
        BigDecimal dropOffDetourKm,
        String rejectionReason,
        OffsetDateTime requestedAt,
        OffsetDateTime respondedAt
) {
}
