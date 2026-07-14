package com.deliveryplatform.bookings.dto;

import com.deliveryplatform.bookings.BookingStatus;
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
        BigDecimal price,
        BookingStatus state,
        OffsetDateTime createdAt,
        OffsetDateTime paidAt,
        OffsetDateTime completedAt,
        OffsetDateTime cancelledAt
) {
}
