package com.deliveryplatform.auth;

import com.deliveryplatform.auth.exceptions.AuthErrorCode;
import com.deliveryplatform.auth.exceptions.AuthException;
import com.deliveryplatform.auth.jwt.JwtConfig;
import com.deliveryplatform.auth.jwt.JwtService;
import com.deliveryplatform.common.caching.CachingService;
import com.deliveryplatform.users.UserPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@AllArgsConstructor
@Service
public class AuthServiceImp implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CachingService cachingService;
    private final JwtConfig jwtConfig;

    private static final String REFRESH_TOKEN_PREFIX = "REFRESH_TOKEN:";


    @Override
    public AuthResponse login(AuthRequest request) {

        var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var principal = (UserPrincipal) auth.getPrincipal();

        var accessToken = jwtService.generateAccessToken(principal);
        var refreshToken = jwtService.generateRefreshToken(principal);

        var key = REFRESH_TOKEN_PREFIX + principal.getId().toString();
        cachingService.save(key, refreshToken , Duration.ofSeconds(jwtConfig.getRefreshTokenDuration()));

        return new AuthResponse(accessToken, refreshToken, jwtConfig.getRefreshTokenDuration());
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        validateRefreshTokenOrThrow(refreshToken);
        var principal = jwtService.extractPrincipal(refreshToken);

        // Refresh Token Rotation for more security
        var newRefreshToken = jwtService.generateRefreshToken(principal);
        var key = REFRESH_TOKEN_PREFIX + principal.getId().toString();
        cachingService.save(key, newRefreshToken, Duration.ofSeconds(jwtConfig.getRefreshTokenDuration()));

        var newAccessToken = jwtService.generateAccessToken(principal);
        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                jwtConfig.getRefreshTokenDuration()
        );
    }

    @Override
    public void logout(UUID userId) {
        var key = REFRESH_TOKEN_PREFIX + userId.toString();
        cachingService.remove(key);
    }


    // --------------------------------------------------------------------


    private void validateRefreshTokenOrThrow(String refreshToken) {
        if (!jwtService.isValid(refreshToken)) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_EXPIRED, "Refresh token expired or malformed");
        }

        var userId = jwtService.getUserIdFromToken(refreshToken);
        var key = REFRESH_TOKEN_PREFIX + userId.toString();
        if (!cachingService.isValid(key, refreshToken)) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_INVALID, "Refresh token has been revoked");
        }
    }
}