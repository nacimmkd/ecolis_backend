package com.deliveryplatform.requests.dto;

import com.deliveryplatform.requests.BookingRequestStatus;
import com.deliveryplatform.parcels.dto.ParcelSummary;
import com.deliveryplatform.trips.dto.TripSummary;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record RequestDto(
        UUID requestId,
        TripSummary trip,
        ParcelSummary parcel,
        BookingRequestStatus status,
        String rejectionReason,
        OffsetDateTime requestedAt,
        OffsetDateTime respondedAt
) {}
