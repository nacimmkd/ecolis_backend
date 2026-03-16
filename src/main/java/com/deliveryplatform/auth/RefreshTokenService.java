package com.deliveryplatform.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtConfig jwtConfig;

    public void save(UUID userId, String token) {
        var RefreshToken = com.deliveryplatform.auth.RefreshToken.builder()
                .userId(userId)
                .token(token)
                .ttl(jwtConfig.getRefreshTokenExpiration())
                .build();
        refreshTokenRepository.save(RefreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public void remove(UUID userId) {
        refreshTokenRepository.deleteById(userId);
    }
}
