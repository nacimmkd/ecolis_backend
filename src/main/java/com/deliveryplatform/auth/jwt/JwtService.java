package com.deliveryplatform.auth.jwt;

import com.deliveryplatform.users.Role;
import com.deliveryplatform.users.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;
    private SecretKey signingKey;

    @PostConstruct
    void init() {
        this.signingKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }

    public Jwt generateAccessToken(UserPrincipal user) {
        return generateToken(user, Duration.ofSeconds(jwtConfig.getAccessTokenDuration()));
    }

    public Jwt generateRefreshToken(UserPrincipal user) {
        return generateToken(user, Duration.ofSeconds(jwtConfig.getRefreshTokenDuration()));
    }

    public boolean isValid(String token) {
        try {
            var claims = parseClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public UUID getUserIdFromToken(String token) {
        return UUID.fromString(parseClaims(token).getSubject());
    }

    public UserPrincipal extractPrincipal(String token) {
        var claims = parseClaims(token);

        return new UserPrincipal(
                UUID.fromString(claims.getSubject()),
                claims.get("email", String.class),
                Role.valueOf(claims.get("role", String.class)),
                null,
                claims.get("verified", Boolean.class)
        );
    }

    // ---------------------------------------------------------------------

    private Jwt generateToken(UserPrincipal user, Duration durationInSeconds) {
        var token = Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .claim("verified", user.isVerified())
                .signWith(signingKey)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + durationInSeconds.toMillis()))
                .compact();

        return new Jwt(token, durationInSeconds);
    }

    private Claims parseClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}