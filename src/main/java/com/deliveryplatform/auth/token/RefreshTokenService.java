package com.deliveryplatform.auth.token;

import com.deliveryplatform.config.JwtConfig;
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
        var RefreshToken = new RefreshToken(
                userId,
                token,
                jwtConfig.getRefreshTokenExpiration()
        );
        refreshTokenRepository.save(RefreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public void remove(UUID userId) {
        refreshTokenRepository.deleteById(userId);
    }
}
