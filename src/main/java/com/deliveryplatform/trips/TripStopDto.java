package com.deliveryplatform.trips;


import com.deliveryplatform.common.addresses.Address;
import com.deliveryplatform.common.addresses.GeoAddress;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class TripStopDto {

    public record StopRequest(
            @NotNull @Min(1) Integer stopOrder,
            @Valid @NotNull Address address
    ) {}

    public record StopResponse(
            UUID          id,
            Integer       stopOrder,
            GeoAddress address
    ) {}
}
