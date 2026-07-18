package com.deliveryplatform.users;

import com.deliveryplatform.auth.AuthService;
import com.deliveryplatform.common.caching.CachingService;
import com.deliveryplatform.common.CodeGeneratorUtil;
import com.deliveryplatform.common.exceptions.ConflictException;
import com.deliveryplatform.common.exceptions.InvalidCredentialsException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.profiles.Profile;
import com.deliveryplatform.users.dto.UpdatePasswordRequest;
import com.deliveryplatform.users.dto.UserCreateRequest;
import com.deliveryplatform.users.dto.UserDetails;
import com.deliveryplatform.users.dto.UserSummary;
import com.deliveryplatform.users.events.EmailVerificationEvent;
import com.deliveryplatform.users.events.UserCreatedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;
    private final ApplicationEventPublisher eventPublisher;


    @Override
    public UserDetails findById(UUID id) {
        return userMapper.toDetailsDto(getUserByIdOrThrow(id));
    }

    @Override
    public List<UserSummary> findAll() {
        return userRepository.findAll().stream()
                .filter(user -> !user.getRole().equals(Role.ADMIN))
                .map(userMapper::toSummaryDto)
                .toList();
    }


    @Override
    @Transactional
    public UserDetails register(UserCreateRequest request) {
        assertEmailUniqueness(request.email());

        var profile = Profile.createFromRequest(request.profile());
        var user = User.create(
                request.email(),
                passwordEncoder.encode(request.password()),
                profile
        );
        userRepository.save(user);
        emailVerificationService.sendCode(user);
        return userMapper.toDetailsDto(user);
    }

    @Override
    public void sendVerificationCode(UUID id) {
        var user = getUserByIdOrThrow(id);
        if (user.isVerified()) throw new ConflictException("User is already verified");
        emailVerificationService.sendCode(user);
    }


    @Override
    @Transactional
    public void verify(UUID userId, String code) {
        var user = getUserByIdOrThrow(userId);
        emailVerificationService.verifyCode(user, code);
        user.verify();
        userRepository.save(user);
        eventPublisher.publishEvent(new UserCreatedEvent(user));
    }


    @Override
    @Transactional
    public void updatePassword(UUID id, UpdatePasswordRequest request) {
        User user = getUserByIdOrThrow(id);
        assertPasswordMatch(request.currentPassword(), user.getPassword());
        user.updatePassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void softDelete(UUID id) {
        User user = getUserByIdOrThrow(id);
        user.delete();
        // delete related things later (profile, parcels,trips ...)
        authService.logout(user.getId());
        userRepository.save(user);
    }


    // ----------------------------------------------------------------

    public User getUserByIdOrThrow(UUID id) {
        return userRepository.findUserWithProfileById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void assertEmailUniqueness(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Account with this email already exists");
        }
    }

    private void assertPasswordMatch(String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, newPassword)) {
            throw new InvalidCredentialsException("old password doesn't match");
        }
    }
}

