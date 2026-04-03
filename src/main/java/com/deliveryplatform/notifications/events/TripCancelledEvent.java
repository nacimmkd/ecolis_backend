package com.deliveryplatform.notifications.events;

import java.time.LocalDate;
import java.util.UUID;

public record TripCancelledEvent(
        UUID userId,
        String userEmail,
        String departureCity,
        String arrivalCity,
        LocalDate departureDate,
        UUID tripId
) {}
