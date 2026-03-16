package com.deliveryplatform.auth;

public record Login(
        String accessToken,
        String refreshToken,
        int refreshExpiration
) {
}
