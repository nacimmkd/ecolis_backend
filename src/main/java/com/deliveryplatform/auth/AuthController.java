package com.deliveryplatform.auth;

import com.deliveryplatform.users.UserPrincipal;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid @RequestBody AuthRequest request,
            HttpServletResponse response
    ) {
        var authResponse = authService.login(request);
        cookieService.setAuthCookies(response, authResponse);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(
            @CookieValue(name = CookieService.REFRESH_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        var authResponse = authService.refresh(refreshToken);
        cookieService.setAuthCookies(response, authResponse);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletResponse response
    ) {
        authService.logout(principal.getId());
        cookieService.clearAuthCookies(response);
        return ResponseEntity.noContent().build();
    }
}