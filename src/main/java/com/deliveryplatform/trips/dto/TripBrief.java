package com.deliveryplatform.trips.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record TripBrief(
        UUID tripId,
        String departureCity,
        String arrivalCity,
        LocalDate departureDate,
        LocalDate arrivalDate
) {
}
