package com.deliveryplatform.auth.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtConfig jwtConfig;

    public void save(UUID userId, String token) {
        var refreshToken = new RefreshToken(
                userId,
                token,
                jwtConfig.getRefreshTokenDuration()
        );
        refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public void remove(UUID userId) {
        refreshTokenRepository.deleteById(userId);
    }
}
