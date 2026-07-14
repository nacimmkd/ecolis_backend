package com.deliveryplatform.users.events;

public record EmailVerificationEvent(
        String userEmail,
        String firstName,
        String code
) {
}
