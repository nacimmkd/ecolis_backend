package com.deliveryplatform.users.dto;

import com.deliveryplatform.profiles.dto.ProfileDetails;
import com.deliveryplatform.users.Role;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record UserDto(
        UUID userId,
        String email,
        Role role,
        boolean verified,
        ProfileDetails profile,
        OffsetDateTime registeredAt
) {}
