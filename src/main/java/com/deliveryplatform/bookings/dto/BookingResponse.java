package com.deliveryplatform.bookings.dto;

import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.bookings.BookingStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BookingResponse(
        UUID bookingId,
        UUID tripId,
        UUID parcelId,
        BigDecimal price,
        BookingStatus status,
        OffsetDateTime confirmedAt,
        OffsetDateTime paidAt,
        OffsetDateTime completedAt,
        OffsetDateTime cancelledAt
) {

    public static BookingResponse of(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getTrip().getId(),
                booking.getParcel().getId(),
                booking.getPrice(),
                booking.getStatus(),
                booking.getConfirmedAt(),
                booking.getPaidAt(),
                booking.getCompletedAt(),
                booking.getCancelledAt()
        );
    }
}
