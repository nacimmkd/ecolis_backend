package com.deliveryplatform.users.dto;

import com.deliveryplatform.profiles.dto.ProfileDto;
import com.deliveryplatform.users.Role;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record UserDetails(
        UUID userId,
        String email,
        Role role,
        boolean emailVerified,
        OffsetDateTime registeredAt,
        ProfileDto profile
) {}
