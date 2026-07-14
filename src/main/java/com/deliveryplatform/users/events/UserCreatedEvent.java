package com.deliveryplatform.users.events;

public record UserCreatedEvent(
        String userEmail,
        String firstName
) {
}
