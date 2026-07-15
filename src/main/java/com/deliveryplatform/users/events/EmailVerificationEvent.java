package com.deliveryplatform.users.events;

import com.deliveryplatform.users.User;

public record EmailVerificationEvent(
        User user,
        String code
) {
}
