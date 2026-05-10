package com.deliveryplatform.bookings.dto;

import com.deliveryplatform.bookings.BookingStatus;
import com.deliveryplatform.parcels.dto.ParcelSummary;
import com.deliveryplatform.trips.dto.TripSummary;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record BookingDto(
        UUID bookingId,
        TripSummary trip,
        ParcelSummary parcel,
        BigDecimal price,
        BookingStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime paidAt,
        OffsetDateTime completedAt,
        OffsetDateTime cancelledAt
) {}
