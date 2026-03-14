package com.deliveryplatform.auth;

import com.deliveryplatform.users.User;
import com.deliveryplatform.users.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (UUID) authentication.getPrincipal();
        return userRepository.findById(userId).orElse(null);
    }
}
