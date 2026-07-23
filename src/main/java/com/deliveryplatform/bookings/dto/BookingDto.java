package com.deliveryplatform.bookings.dto;

import com.deliveryplatform.bookings.BookingState;
import com.deliveryplatform.parcels.dto.ParcelSummary;
import com.deliveryplatform.trips.dto.TripSummary;
import com.deliveryplatform.users.dto.UserBrief;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record BookingDto(
        UUID bookingId,
        TripSummary trip,
        ParcelSummary parcel,
        UserBrief sender,
        UserBrief carrier,
        BigDecimal price,
        BookingState state,
        OffsetDateTime createdAt,
        OffsetDateTime paidAt,
        OffsetDateTime completedAt,
        OffsetDateTime cancelledAt
) {}
