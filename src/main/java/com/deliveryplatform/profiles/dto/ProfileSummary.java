package com.deliveryplatform.profiles.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ProfileSummary(
        UUID profileId,
        String firstName,
        String lastName,
        String phone,
        String country,
        String avatarUrl
) {}
