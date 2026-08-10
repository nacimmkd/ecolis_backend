package com.deliveryplatform.auth;

import com.deliveryplatform.auth.exceptions.AuthErrorCode;
import com.deliveryplatform.auth.exceptions.AuthException;
import com.deliveryplatform.auth.jwt.JwtConfig;
import com.deliveryplatform.auth.jwt.JwtService;
import com.deliveryplatform.common.caching.CachingService;
import com.deliveryplatform.users.UserMapper;
import com.deliveryplatform.users.UserPrincipal;
import com.deliveryplatform.users.UserRepository;
import com.deliveryplatform.users.dto.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class AuthServiceImp implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CachingService cachingService;
    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public AuthResponse login(AuthRequest request) {
        var principal = authenticate(request);

        var accessToken = jwtService.generateAccessToken(principal);
        var refreshToken = jwtService.generateRefreshToken(principal);
        var user = userMapper.toDetailsDto(userRepository.findUserById(principal.getId()).orElse(null));

        storeRefreshToken(principal.getId(), refreshToken.token());

        return new AuthResponse(accessToken, refreshToken, user);
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        validateRefreshTokenOrThrow(refreshToken);

        var principal = jwtService.extractPrincipal(refreshToken);

        var newRefreshToken = jwtService.generateRefreshToken(principal);
        storeRefreshToken(principal.getId(), newRefreshToken.token());

        var newAccessToken = jwtService.generateAccessToken(principal);

        var user = userMapper.toDetailsDto(userRepository.findUserById(principal.getId()).orElse(null));
        return new AuthResponse(newAccessToken, newRefreshToken,user);
    }

    @Override
    public void logout(UUID userId) {
        cachingService.remove(refreshTokenCacheKey(userId));
        SecurityContextHolder.clearContext();
    }

    @Override
    public UserPrincipal getCurrentUserPrincipal() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new AuthException(AuthErrorCode.USER_NOT_AUTHENTICATED, "not authenticated");
        }

        return principal;
    }

    @Override
    public UserDetails getMe() {
        var principal = this.getCurrentUserPrincipal();
        var userId = principal.getId();
        var user = userRepository.findUserById(userId).orElse(null);
        return userMapper.toDetailsDto(user);
    }

    // ---------------------------------------------------------------------

    private UserPrincipal authenticate(AuthRequest request) {
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            return (UserPrincipal) auth.getPrincipal();
        } catch (BadCredentialsException e) {
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS, "Invalid email or password");
        } catch (DisabledException e) {
            throw new AuthException(AuthErrorCode.USER_NOT_VERIFIED, "account is not verified");
        }
    }

    private void storeRefreshToken(UUID userId, String token) {
        cachingService.save(
                refreshTokenCacheKey(userId),
                token,
                Duration.ofSeconds(jwtConfig.getRefreshTokenDuration()));
    }

    private void validateRefreshTokenOrThrow(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_INVALID, "Refresh token missing");
        }
        if (!jwtService.isValid(refreshToken)) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_EXPIRED, "Refresh token expired or malformed");
        }
        var userId = jwtService.getUserIdFromToken(refreshToken);
        if (!cachingService.isValid(refreshTokenCacheKey(userId), refreshToken)) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_INVALID, "Refresh token has been revoked");
        }
    }

    private String refreshTokenCacheKey(UUID userId) {
        return CookieService.REFRESH_COOKIE_NAME + ":" +  userId;
    }
}