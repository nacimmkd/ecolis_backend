package com.deliveryplatform.trips;

import com.deliveryplatform.common.addresses.Address;
import com.deliveryplatform.common.addresses.AddressRequest;
import com.deliveryplatform.trips.TripStopDto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class TripDto {

    public record TripRequest(
            @Valid @NotNull AddressRequest departure,
            @Valid @NotNull AddressRequest arrival,

            @NotNull @FutureOrPresent LocalDate departureDate,
            @NotNull @FutureOrPresent LocalDate arrivalDate,
            @DecimalMin("0.0") BigDecimal maxVolumeM3,
            @DecimalMin("0.0") BigDecimal maxWeightKg,
            @DecimalMin("0.0") BigDecimal price,
            @DecimalMin("0.0") BigDecimal maxDetourKm,

            String notes,

            @Valid List<TripStopRequest> stops
    ) {}

    public record TripResponse(
            UUID id,
            UUID userId,
            Address departure,
            Address arrival,
            LocalDate departureDate,
            LocalDate arrivalDate,
            BigDecimal maxVolumeM3,
            BigDecimal maxWeightKg,
            BigDecimal price,
            TripStatus status,
            String notes,
            List<TripStopResponse> stops
    ) {}

}