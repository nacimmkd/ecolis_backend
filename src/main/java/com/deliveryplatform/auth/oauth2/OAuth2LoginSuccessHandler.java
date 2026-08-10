package com.deliveryplatform.auth.oauth2;

import com.deliveryplatform.auth.AuthProvider;
import com.deliveryplatform.auth.AuthResponse;
import com.deliveryplatform.auth.CookieService;
import com.deliveryplatform.auth.jwt.JwtConfig;
import com.deliveryplatform.auth.jwt.JwtService;
import com.deliveryplatform.common.caching.CachingService;
import com.deliveryplatform.profiles.Profile;
import com.deliveryplatform.users.*;
import com.deliveryplatform.users.events.UserCreatedEvent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final CachingService cachingService;
    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;
    private final CookieService cookieService;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${front-end.base-url}")
    private String frontHomeUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String firstName = oauthUser.getAttribute("given_name");
        String lastName = oauthUser.getAttribute("family_name");

        Boolean emailVerified = oauthUser.getAttribute("email_verified");
        if (email == null || !Boolean.TRUE.equals(emailVerified)) {
            response.sendRedirect(frontHomeUrl + "/login?status=error&reason=email_not_verified");
            return;
        }

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User created = userRepository.save(User.create(
                            email,
                            null,
                            true,
                            AuthProvider.GOOGLE,
                            Profile.create(firstName, lastName)
                    ));
                    eventPublisher.publishEvent(new UserCreatedEvent(created));
                    return created;
                });

        var principal = UserPrincipal.from(user);

        var accessToken = jwtService.generateAccessToken(principal);
        var refreshToken = jwtService.generateRefreshToken(principal);

        cachingService.save(
                CookieService.REFRESH_COOKIE_NAME + ":" + principal.getId(),
                refreshToken.token(),
                Duration.ofSeconds(jwtConfig.getRefreshTokenDuration())
        );

        cookieService.setAuthCookies(response, new AuthResponse(accessToken, refreshToken, null));
        response.sendRedirect(frontHomeUrl + "?status=success");
    }
}