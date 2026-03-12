package com.deliveryplatform.users;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
        UUID id,
        String        firstName,
        String        lastName,
        String        email,
        String        phone,
        String        avatarUrl,
        Role          role,
        boolean       isVerified,
        boolean       isActive,
        LocalDateTime registeredAt
) {}
