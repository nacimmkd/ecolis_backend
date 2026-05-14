package com.deliveryplatform.profiles.dto;

import com.deliveryplatform.images.dto.ImageDto;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ProfileSummary(
        UUID profileId,
        String firstName,
        String lastName,
        String phone,
        ImageDto avatar
) {}
