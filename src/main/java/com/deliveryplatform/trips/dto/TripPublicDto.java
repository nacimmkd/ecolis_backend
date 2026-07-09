package com.deliveryplatform.trips.dto;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.trips.TripState;
import com.deliveryplatform.users.dto.UserBrief;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Builder
public record TripPublicDto(
        UUID tripId,
        UserBrief owner,
        Address departureAddress,
        Address arrivalAddress,
        LocalDate departureDate,
        LocalDate arrivalDate,
        BigDecimal availableWeightKg,
        BigDecimal remainingWeightKg,
        BigDecimal pricePerKg,
        boolean instantBooking,
        TripState status,
        String notes,
        List<TripStopDto> stops
) {}
