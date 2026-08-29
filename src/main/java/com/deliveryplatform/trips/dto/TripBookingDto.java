package com.deliveryplatform.trips.dto;

import com.deliveryplatform.bookings.BookingState;
import com.deliveryplatform.payments.Price;
import com.deliveryplatform.parcels.dto.ParcelSummary;
import com.deliveryplatform.profiles.dto.ProfileBrief;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record TripBookingDto(
        UUID bookingId,
        ParcelSummary parcel,
        ProfileBrief sender,
        Price price,
        BookingState state,
        BigDecimal pickupDetourKm,
        BigDecimal dropOffDetourKm,
        String rejectionReason,
        OffsetDateTime createdAt,
        OffsetDateTime respondedAt,
        OffsetDateTime completedAt,
        OffsetDateTime cancelledAt
) {}