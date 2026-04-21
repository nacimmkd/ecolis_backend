package com.deliveryplatform.users.dto;

import com.deliveryplatform.users.Role;
import com.deliveryplatform.users.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record UserResponse(
        UUID          id,
        String        email,
        Role          role,
        boolean       isVerified,
        boolean       isActive,
        OffsetDateTime registeredAt
) {

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .isVerified(user.isVerified())
                .isActive(user.isActive())
                .registeredAt(user.getRegisteredAt())
                .build();
    }
}
