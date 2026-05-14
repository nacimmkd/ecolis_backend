package com.deliveryplatform.users.dto;

import com.deliveryplatform.profiles.dto.ProfileSummary;
import com.deliveryplatform.users.Role;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record UserDetails(
        UUID userId,
        String email,
        Role role,
        boolean verified,
        ProfileSummary profile,
        OffsetDateTime registeredAt
) {}
