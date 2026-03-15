package com.deliveryplatform.auth.tokens;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("refresh_token")
public class Token {

    @Id
    private UUID userId;

    private String token;

    private LocalDateTime expiryDate;
}