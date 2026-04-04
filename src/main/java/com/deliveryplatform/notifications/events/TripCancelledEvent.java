package com.deliveryplatform.notifications.events;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record TripCancelledEvent(
        UUID userId,
        String userEmail,
        String departureCity,
        String arrivalCity,
        LocalDate departureDate,
        UUID tripId
) {}
