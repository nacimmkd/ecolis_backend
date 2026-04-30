package com.deliveryplatform.bookings.dto;

import com.deliveryplatform.bookings.BookingRequest;
import com.deliveryplatform.bookings.BookingRequestStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BookingRequestResponse(
        UUID requestId,
        UUID tripId,
        UUID parcelId,
        BigDecimal price,
        BookingRequestStatus status,
        String rejectionReason,
        OffsetDateTime requestedAt,
        OffsetDateTime respondedAt
) {
    public static BookingRequestResponse of(BookingRequest request) {
        return new BookingRequestResponse(
                request.getId(),
                request.getTrip().getId(),
                request.getParcel().getId(),
                request.getPrice(),
                request.getStatus(),
                request.getRejectionReason(),
                request.getRequestedAt(),
                request.getRespondedAt()
        );
    }
}
