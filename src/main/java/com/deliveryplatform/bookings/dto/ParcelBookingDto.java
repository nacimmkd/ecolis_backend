package com.deliveryplatform.bookings.dto;

import com.deliveryplatform.bookings.BookingStatus;
import com.deliveryplatform.trips.dto.TripPublicDto;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record ParcelBookingDto(
        UUID bookingId,
        TripPublicDto trip,
        BigDecimal price,
        BookingStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime paidAt,
        OffsetDateTime completedAt,
        OffsetDateTime cancelledAt
) {
}
