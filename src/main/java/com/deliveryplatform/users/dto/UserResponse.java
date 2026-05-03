package com.deliveryplatform.users.dto;

import com.deliveryplatform.profiles.dto.ProfileResponse;
import com.deliveryplatform.users.Role;
import com.deliveryplatform.users.User;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record UserResponse(
        UUID userId,
        String email,
        Role role,
        boolean isVerified,
        boolean isDeleted,
        ProfileResponse profile,
        OffsetDateTime registeredAt,
        OffsetDateTime deletedAt
) {

    public UserResponse withProfile(ProfileResponse profile) {
        return new UserResponse(userId, email, role, isVerified,
                isDeleted, profile, registeredAt, deletedAt);
    }
}
