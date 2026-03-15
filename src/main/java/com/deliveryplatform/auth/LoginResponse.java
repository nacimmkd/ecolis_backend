package com.deliveryplatform.auth;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        int refreshExpiration
) {
}
