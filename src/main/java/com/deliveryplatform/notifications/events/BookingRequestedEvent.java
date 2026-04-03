package com.deliveryplatform.notifications.events;

import java.util.UUID;

public record BookingRequestedEvent(
        UUID carrierId,
        String carrierEmail,
        String departureCity,
        String arrivalCity,
        UUID bookingId
) {}
