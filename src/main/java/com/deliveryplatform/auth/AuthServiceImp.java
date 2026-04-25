package com.deliveryplatform.auth;

import com.deliveryplatform.auth.jwt.JwtConfig;
import com.deliveryplatform.auth.jwt.JwtService;
import com.deliveryplatform.caching.CachingService;
import com.deliveryplatform.common.exceptions.AuthenticationException;
import com.deliveryplatform.users.UserPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

        var auth = authenticate(request.getEmail(), request.getPassword());
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
        var principal = getPrincipalFromRefreshToken(refreshToken);

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

    private Authentication authenticate(String email, String password) {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }

    private UserPrincipal getPrincipalFromRefreshToken(String refreshToken) {
        return jwtService.extractPrincipal(refreshToken);
    }

    private void validateRefreshTokenOrThrow(String refreshToken) {
        var userId = jwtService.getUserIdFromToken(refreshToken);
        var key = REFRESH_TOKEN_PREFIX + userId.toString();
        var isValid =  cachingService.isValid(key, refreshToken) && jwtService.isValidToken(refreshToken);
        if (!isValid) {
            throw new AuthenticationException("Session expired");
        }
    }
}