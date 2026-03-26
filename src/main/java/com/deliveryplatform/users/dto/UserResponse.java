package com.deliveryplatform.users.dto;

import com.deliveryplatform.profiles.dto.ProfileResponse;
import com.deliveryplatform.users.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String        email,
        Role role,
        boolean       isVerified,
        boolean       isActive,
        LocalDateTime registeredAt,
        ProfileResponse profile
) {}
