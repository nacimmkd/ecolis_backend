package com.deliveryplatform.bookings.dto;

import com.deliveryplatform.bookings.BookingState;
import com.deliveryplatform.payments.Price;
import com.deliveryplatform.trips.dto.TripSummary;
import com.deliveryplatform.users.dto.UserBrief;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record ParcelBookingDto(
        UUID bookingId,
        TripSummary trip,
        UserBrief carrier,
        Price price,
        BookingState state,
        BigDecimal pickupDetourKm,
        BigDecimal dropOffDetourKm,
        String rejectionReason,
        OffsetDateTime createdAt,
        OffsetDateTime respondedAt,
        OffsetDateTime completedAt,
        OffsetDateTime cancelledAt
) {
}
