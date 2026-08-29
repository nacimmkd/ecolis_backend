package com.deliveryplatform.users;

import com.deliveryplatform.auth.AuthService;
import com.deliveryplatform.profiles.Profile;
import com.deliveryplatform.users.dto.UpdatePasswordRequest;
import com.deliveryplatform.users.dto.UserCreateRequest;
import com.deliveryplatform.users.dto.UserDetails;
import com.deliveryplatform.users.dto.UserSummary;
import com.deliveryplatform.users.events.EmailVerificationEvent;
import com.deliveryplatform.users.events.UserCreatedEvent;
import com.deliveryplatform.users.exceptions.UserErrorCode;
import com.deliveryplatform.users.exceptions.UserException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthService authService;
    private final TokenService emailTokenService;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${front-end.reset-password-url}")
    private String resetPasswordUrl;

    @Value("${front-end.verify-email-url}")
    private String verifyEmailUrl;

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

        var existingUser = userRepository.findUserByEmail(request.email()).orElse(null);

        if (existingUser != null && !existingUser.isEmailVerified())
            throw new UserException(UserErrorCode.USER_NOT_VERIFIED, "user exists but not emailVerified");

        assertEmailUniqueness(request.email());

        var profile = Profile.create(request.firstName(), request.lastName(), null);
        var user = User.create(
                request.email(),
                passwordEncoder.encode(request.password()),
                false,
                profile
        );
        userRepository.save(user);
        return userMapper.toDetailsDto(user);
    }

    @Override
    @Transactional
    public void requestEmailVerification(String email) {
        var user = getUserByEmailOrThrow(email);
        if (user.isEmailVerified()) {
            throw new UserException(UserErrorCode.USER_ALREADY_VERIFIED, "User is already emailVerified");
        }
        send(user, verifyEmailUrl);
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        var userId = emailTokenService.verifyToken(token);
        var user = getUserByIdOrThrow(userId);
        user.verify();
        userRepository.save(user);
        eventPublisher.publishEvent(new UserCreatedEvent(user));
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        var user = userRepository.findUserByEmail(email).orElse(null);
        if (user == null) {
            return;
        }
        send(user, resetPasswordUrl);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        var userId = emailTokenService.verifyToken(token);
        var user = getUserByIdOrThrow(userId);

        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        authService.logout(user.getId());
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
        // delete related things later (profile, parcels, trips ...)
        authService.logout(user.getId());
        userRepository.save(user);
    }

    // ----------------------------------------------------------------

    private void send(User user, String prefixUrl) {
        var token = emailTokenService.generateAndSave(user);
        var url = prefixUrl + "?token=" + token;
        eventPublisher.publishEvent(new EmailVerificationEvent(user, url));
    }

    public User getUserByIdOrThrow(UUID id) {
        return userRepository.findUserById(id)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND, "User not found"));
    }

    public User getUserByEmailOrThrow(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND, "User not found"));
    }

    private void assertEmailUniqueness(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserException(UserErrorCode.EMAIL_ALREADY_EXISTS, "A user with this email already exists");
        }
    }

    private void assertPasswordMatch(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD, "Current password does not match");
        }
    }
}