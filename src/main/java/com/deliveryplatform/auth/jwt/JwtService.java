package com.deliveryplatform.auth.jwt;

import com.deliveryplatform.users.Role;
import com.deliveryplatform.users.User;
import com.deliveryplatform.users.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;
    private final RefreshTokenService refreshTokenService;


    public String generateAccessToken(UserPrincipal user) {
        return generateToken(user, jwtConfig.getAccessTokenDuration());
    }

    public String generateRefreshToken(UserPrincipal user) {
        return generateToken(user, jwtConfig.getRefreshTokenDuration());
    }

    public boolean validateRefreshToken(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .map(token -> validateToken(token.getToken()))
                .orElse(false);
    }

    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken);
    }


    public UUID getUserIdFromToken(String token){
        return UUID.fromString(parseClaims(token).getSubject());
    }

    public String getEmailFromToken(String token){
        return parseClaims(token).get("email", String.class);
    }


    public Role getRoleFromToken(String token){
        return Role.valueOf(parseClaims(token).get("role", String.class));
    }


    public UserPrincipal extractPrincipal(String token) {
        return new UserPrincipal(
                getUserIdFromToken(token),
                getEmailFromToken(token),
                getRoleFromToken(token),
                null);
    }
    // ---------------------------------------------------------------------

    private boolean validateToken(String token){
        try {
            var claims = parseClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

    private String generateToken(UserPrincipal user, int expiration) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .signWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (expiration * 1000L)))
                .compact();
    }

    private Claims parseClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}
