package com.deliveryplatform.users.dto;

import com.deliveryplatform.profiles.dto.ProfileResponse;
import com.deliveryplatform.users.Role;
import com.deliveryplatform.users.User;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record UserSummaryResponse(
        UUID id,
        String email,
        Role role,
        boolean isVerified,
        boolean isDeleted,
        OffsetDateTime registeredAt,
        OffsetDateTime deletedAt
) {

    public static UserSummaryResponse of(User user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .isVerified(user.isVerified())
                .isDeleted(user.isDeleted())
                .registeredAt(user.getRegisteredAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }
}