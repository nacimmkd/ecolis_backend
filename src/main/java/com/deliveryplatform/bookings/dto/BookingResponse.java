package com.deliveryplatform.bookings.dto;

import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.bookings.BookingStatus;
import com.deliveryplatform.parcels.dto.ParcelSummaryResponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BookingResponse(
        UUID bookingId,
        UUID tripId,
        ParcelSummaryResponse parcel,
        BigDecimal price,
        BookingStatus status,
        OffsetDateTime confirmedAt,
        OffsetDateTime paidAt,
        OffsetDateTime completedAt,
        OffsetDateTime cancelledAt
) {

    public static BookingResponse of(Booking booking, ParcelSummaryResponse parcel) {
        return new BookingResponse(
                booking.getId(),
                booking.getTrip().getId(),
                parcel,
                booking.getPrice(),
                booking.getStatus(),
                booking.getConfirmedAt(),
                booking.getPaidAt(),
                booking.getCompletedAt(),
                booking.getCancelledAt()
        );
    }
}
