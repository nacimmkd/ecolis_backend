package com.deliveryplatform.users.events;

import com.deliveryplatform.users.User;

public record UserCreatedEvent(
        User user
) {
 }
