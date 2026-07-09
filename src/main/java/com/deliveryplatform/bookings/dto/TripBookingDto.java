package com.deliveryplatform.bookings.dto;

import com.deliveryplatform.bookings.BookingStatus;
import com.deliveryplatform.parcels.dto.ParcelPublicDto;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record TripBookingDto(
        UUID bookingId,
        ParcelPublicDto parcel,
        BigDecimal price,
        BookingStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime paidAt,
        OffsetDateTime completedAt,
        OffsetDateTime cancelledAt
) {}
