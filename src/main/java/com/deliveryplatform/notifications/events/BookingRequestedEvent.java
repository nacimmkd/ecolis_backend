package com.deliveryplatform.notifications.events;

import lombok.Builder;

import java.util.UUID;

@Builder
public record BookingRequestedEvent(
        UUID carrierId,
        String carrierEmail,
        String departureCity,
        String arrivalCity,
        UUID bookingId
) {}
