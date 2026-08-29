package com.deliveryplatform.common.config;

import com.deliveryplatform.auth.CookieService;
import com.deliveryplatform.auth.jwt.JwtService;
import com.deliveryplatform.users.UserPrincipal;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Arrays;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor, HandshakeInterceptor {

    private static final String PRINCIPAL_ATTRIBUTE = "principal";

    private final JwtService jwtService;

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                                    @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            var token = extractTokenFromCookie(servletRequest.getServletRequest());
            if (token != null && jwtService.isValid(token)) {
                attributes.put(PRINCIPAL_ATTRIBUTE, jwtService.extractPrincipal(token));
            } else {
                log.warn("[WS] Missing or invalid access token cookie during handshake");
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                                @NonNull WebSocketHandler wsHandler, Exception exception) {
    }

    @Override
    @NonNull
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            authenticateConnection(accessor);
        }
        return message;
    }

    private void authenticateConnection(StompHeaderAccessor accessor) {
        var attributes = accessor.getSessionAttributes();
        var principal = attributes != null ? attributes.get(PRINCIPAL_ATTRIBUTE) : null;

        if (!(principal instanceof UserPrincipal userPrincipal)) {
            log.warn("[WS] No authenticated principal found for connection, was the access token cookie sent?");
            return;
        }

        var auth = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities()) {
            @Override
            public String getName() {
                return userPrincipal.getId().toString();
            }
        };
        accessor.setUser(auth);
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(cookie -> CookieService.ACCESS_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
