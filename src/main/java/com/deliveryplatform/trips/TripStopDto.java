package com.deliveryplatform.trips;


import com.deliveryplatform.common.addresses.AddressRequest;
import com.deliveryplatform.common.addresses.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class TripStopDto {

    public record TripStopRequest(
            @NotNull @Min(1) Integer stopOrder,
            @Valid @NotNull  AddressRequest address
    ) {}

    public record TripStopResponse(
            UUID          id,
            Integer       stopOrder,
            Address address
    ) {}
}
