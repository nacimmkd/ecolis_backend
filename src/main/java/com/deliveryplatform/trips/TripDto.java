package com.deliveryplatform.trips;

import com.deliveryplatform.common.addresses.GeoAddress;
import com.deliveryplatform.common.addresses.Address;
import com.deliveryplatform.trips.TripStopDto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class TripDto {

    public record TripRequest(
            @Valid @NotNull Address departure,
            @Valid @NotNull Address arrival,

            @NotNull @FutureOrPresent LocalDate departureDate,
            @NotNull @FutureOrPresent LocalDate arrivalDate,
            @DecimalMin("0.0") BigDecimal availableVolumeCm3,
            @DecimalMin("0.0") BigDecimal availableWeightKg,
            @NotNull @DecimalMin("0.0") BigDecimal pricePerKg,
            @DecimalMin("0.0") BigDecimal maxDetourKm,

            String notes,

            @Valid List<StopRequest> stops
    ) {}

    public record TripResponse(
            UUID id,
            UUID userId,
            GeoAddress departure,
            GeoAddress arrival,
            LocalDate departureDate,
            LocalDate arrivalDate,
            BigDecimal availableVolumeCm3,
            BigDecimal availableWeightKg,
            BigDecimal pricePerKg,
            TripStatus status,
            String notes,
            List<StopResponse> stops
    ) {}

}