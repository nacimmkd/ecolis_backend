package com.deliveryplatform.users;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
        UUID id,
        String        firstName,
        String        lastName,
        String        email,
        Role          role,
        boolean       isVerified,
        boolean       isActive,
        LocalDateTime registeredAt,
        ProfileDto profile
) {}
