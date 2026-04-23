package com.deliveryplatform.users.dto;

import com.deliveryplatform.profiles.dto.ProfileResponse;
import com.deliveryplatform.users.Role;
import com.deliveryplatform.users.User;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record UserResponse(
        UUID id,
        String email,
        Role role,
        boolean isVerified,
        boolean isDeleted,
        ProfileResponse profile,
        OffsetDateTime registeredAt,
        OffsetDateTime deletedAt
) {

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .isVerified(user.isVerified())
                .isDeleted(user.isDeleted())
                .profile(user.getProfile() != null ? ProfileResponse.of(user.getProfile()) : null)
                .registeredAt(user.getRegisteredAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }
}
