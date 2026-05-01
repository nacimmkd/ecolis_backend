package com.deliveryplatform.bookings.dto;

import com.deliveryplatform.bookings.BookingRequest;
import com.deliveryplatform.bookings.BookingRequestStatus;
import com.deliveryplatform.parcels.dto.ParcelSummaryResponse;

import java.time.OffsetDateTime;
import java.util.UUID;

public record BookingRequestResponse(
        UUID requestId,
        UUID tripId,
        ParcelSummaryResponse parcel,
        BookingRequestStatus status,
        String rejectionReason,
        OffsetDateTime requestedAt,
        OffsetDateTime respondedAt
) {
    public static BookingRequestResponse of(BookingRequest request, ParcelSummaryResponse parcel) {
        return new BookingRequestResponse(
                request.getId(),
                request.getTrip().getId(),
                parcel,
                request.getStatus(),
                request.getRejectionReason(),
                request.getRequestedAt(),
                request.getRespondedAt()
        );
    }
}
