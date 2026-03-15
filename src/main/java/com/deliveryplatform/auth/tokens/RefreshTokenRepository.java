package com.deliveryplatform.auth.tokens;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends CrudRepository<Token, UUID> {
    Optional<Token> findByToken(String token);
}