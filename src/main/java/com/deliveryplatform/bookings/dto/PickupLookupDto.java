package com.deliveryplatform.bookings.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PickupLookupDto(
        UUID bookingId,
        String street,
        String city,
        String postalCode,
        String country,
        double latitude,
        double longitude
) {}