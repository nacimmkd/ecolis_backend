package com.deliveryplatform.parcels.dto;

import com.deliveryplatform.bookings.BookingState;
import com.deliveryplatform.payments.Price;
import com.deliveryplatform.trips.dto.TripSummary;
import com.deliveryplatform.profiles.dto.ProfileBrief;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record ParcelBookingDto(
        UUID bookingId,
        TripSummary trip,
        ProfileBrief carrier,
        Price price,
        BookingState state,
        String pickupCode,
        String dropOffCode,
        BigDecimal pickupDetourKm,
        BigDecimal dropOffDetourKm,
        String rejectionReason,
        OffsetDateTime createdAt,
        OffsetDateTime respondedAt,
        OffsetDateTime completedAt,
        OffsetDateTime cancelledAt
) {
}
