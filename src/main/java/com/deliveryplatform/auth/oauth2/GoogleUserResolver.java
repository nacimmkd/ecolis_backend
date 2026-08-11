package com.deliveryplatform.auth.oauth2;

import com.deliveryplatform.auth.AuthProvider;
import com.deliveryplatform.profiles.Profile;
import com.deliveryplatform.users.User;
import com.deliveryplatform.users.UserRepository;
import com.deliveryplatform.users.events.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GoogleUserResolver {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public User resolve(String email, String firstName, String lastName) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User created = userRepository.save(User.create(
                            email, null, true, AuthProvider.GOOGLE,
                            Profile.create(firstName, lastName)
                    ));
                    eventPublisher.publishEvent(new UserCreatedEvent(created));
                    return created;
                });
    }
}