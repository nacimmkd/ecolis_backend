package com.deliveryplatform.messages.dto;

import com.deliveryplatform.users.User;

import java.util.UUID;

public record ChatUser(
        UUID userId,
        String         fullName,
        String         avatarUrl
) {

    public static ChatUser of(User user) {
        return new ChatUser(
                user.getId(),
                user.getProfile().getFirstName() + " " + user.getProfile().getLastName(),
                "" // to be completed later
        );
    }
}
