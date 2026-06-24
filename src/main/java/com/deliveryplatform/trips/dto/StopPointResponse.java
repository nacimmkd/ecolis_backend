package com.deliveryplatform.trips.dto;

import com.deliveryplatform.addresses.Address;
import lombok.Builder;

import java.util.UUID;

@Builder
public record StopPointResponse(
        UUID id,
        Integer order,
        Address address
) {}