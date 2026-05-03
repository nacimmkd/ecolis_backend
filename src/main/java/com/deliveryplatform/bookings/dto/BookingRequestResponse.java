package com.deliveryplatform.bookings.dto;

import com.deliveryplatform.bookings.BookingRequestStatus;
import com.deliveryplatform.parcels.dto.ParcelSummaryResponse;
import com.deliveryplatform.trips.dto.TripSummaryResponse;

import java.time.OffsetDateTime;
import java.util.UUID;

public record BookingRequestResponse(
        UUID requestId,
        TripSummaryResponse trip,
        ParcelSummaryResponse parcel,
        BookingRequestStatus status,
        String rejectionReason,
        OffsetDateTime requestedAt,
        OffsetDateTime respondedAt
) {

    public BookingRequestResponse withTrip(TripSummaryResponse tripSummary) {
        return new BookingRequestResponse(requestId,tripSummary,parcel,status,rejectionReason,requestedAt,respondedAt);
    }

    public BookingRequestResponse withParcel(ParcelSummaryResponse parcelSummary) {
        return new BookingRequestResponse(requestId, trip, parcelSummary ,status,rejectionReason,requestedAt,respondedAt);
    }
}
