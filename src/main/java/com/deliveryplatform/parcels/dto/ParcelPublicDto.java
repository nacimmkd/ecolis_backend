package com.deliveryplatform.parcels.dto;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.images.dto.ImageDto;
import com.deliveryplatform.parcels.ParcelSize;
import com.deliveryplatform.users.dto.UserBrief;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Builder
public record ParcelPublicDto(
        UUID parcelId,
        UserBrief owner,
        String description,
        BigDecimal weightKg,
        ParcelSize size,
        boolean fragile,
        Address pickupAddress,
        Address dropoffAddress,
        ImageDto thumbnail,
        List<ImageDto> images
) {}
