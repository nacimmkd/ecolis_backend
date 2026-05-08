package com.deliveryplatform.users.dto;

import com.deliveryplatform.users.Role;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record UserSummaryDto(
        UUID userId,
        String email,
        Role role,
        boolean verified,
        OffsetDateTime registeredAt
) {}