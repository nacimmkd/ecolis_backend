package com.deliveryplatform.auth.jwt;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("refresh_token")
public class RefreshToken {

    @Id
    private UUID userId;

    @Indexed
    private String token;

    @TimeToLive
    private int ttl;
}