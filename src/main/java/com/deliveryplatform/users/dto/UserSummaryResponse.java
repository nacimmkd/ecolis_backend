package com.deliveryplatform.users.dto;

import com.deliveryplatform.users.Role;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record UserSummaryResponse(
        UUID userId,
        String email,
        Role role,
        boolean isVerified,
        boolean isDeleted,
        OffsetDateTime registeredAt,
        OffsetDateTime deletedAt
) {}