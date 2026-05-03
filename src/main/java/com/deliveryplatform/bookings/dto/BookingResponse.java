package com.deliveryplatform.bookings.dto;

import com.deliveryplatform.bookings.BookingStatus;
import com.deliveryplatform.parcels.dto.ParcelSummaryResponse;
import com.deliveryplatform.trips.dto.TripSummaryResponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BookingResponse(
        UUID bookingId,
        TripSummaryResponse trip,
        ParcelSummaryResponse parcel,
        BigDecimal price,
        BookingStatus status,
        OffsetDateTime confirmedAt,
        OffsetDateTime paidAt,
        OffsetDateTime completedAt,
        OffsetDateTime cancelledAt
) {

    public BookingResponse withTrip(TripSummaryResponse tripSummary) {
        return new BookingResponse(bookingId, tripSummary, parcel, price, status, confirmedAt, paidAt, completedAt, cancelledAt);
    }

    public BookingResponse withParcels(ParcelSummaryResponse parcelSummary) {
        return new BookingResponse(bookingId, trip, parcelSummary, price, status, confirmedAt, paidAt, completedAt, cancelledAt);
    }
}
