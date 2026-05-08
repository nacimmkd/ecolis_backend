package com.deliveryplatform.bookings.dto;

import com.deliveryplatform.bookings.BookingRequestStatus;
import com.deliveryplatform.parcels.dto.ParcelSummary;
import com.deliveryplatform.trips.dto.TripSummary;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record BookingRequestDto(
        UUID requestId,
        TripSummary trip,
        ParcelSummary parcel,
        BookingRequestStatus status,
        String rejectionReason,
        OffsetDateTime requestedAt,
        OffsetDateTime respondedAt
) {}
