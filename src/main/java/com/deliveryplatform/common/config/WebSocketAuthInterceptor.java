package com.deliveryplatform.common.config;

import com.deliveryplatform.auth.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            authenticateConnection(accessor);
        }
        return message;
    }

    private void authenticateConnection(StompHeaderAccessor accessor) {
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("[WS] Missing or invalid Authorization header");
            return;
        }

        var token = authHeader.replace("Bearer ", "");
        if (!jwtService.validateAccessToken(token)) {
            log.warn("[WS] Invalid token");
            return;
        }

        var principal = jwtService.extractPrincipal(token);
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        accessor.setUser(auth);
    }
}