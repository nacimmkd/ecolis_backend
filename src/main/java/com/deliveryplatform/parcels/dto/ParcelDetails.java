package com.deliveryplatform.parcels.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record ParcelDetails(
        ParcelSummary parcel,
        List<ParcelBookingDto> bookings
){}
