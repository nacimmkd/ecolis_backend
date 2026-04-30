package com.deliveryplatform.users;

import com.deliveryplatform.auth.AuthService;
import com.deliveryplatform.caching.CachingService;
import com.deliveryplatform.common.CodeGeneratorUtil;
import com.deliveryplatform.common.exceptions.ConflictException;
import com.deliveryplatform.common.exceptions.InvalidCredentialsException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.emails.EmailService;
import com.deliveryplatform.emails.EmailTemplates;
import com.deliveryplatform.profiles.dto.ProfilePostRequest;
import com.deliveryplatform.users.dto.UpdatePasswordRequest;
import com.deliveryplatform.users.dto.UserPostRequest;
import com.deliveryplatform.users.dto.UserResponse;
import com.deliveryplatform.users.dto.UserSummaryResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    private final EmailService emailService;
    private final CachingService cachingService;
    private final AuthService authService;

    private static final String VERIFICATION_CODE_PREFIX = "email:verify:";
    private static final Duration VERIFICATION_CODE_TTL = Duration.ofMinutes(5);



    @Override
    public UserResponse findById(UUID id) {
        var user = getUserByIdOrThrow(id);
        return UserResponse.of(user);
    }

    @Override
    public List<UserSummaryResponse> findAll() {
        return userRepository.findAll().stream()
                .filter(user -> !user.getRole().equals(Role.ADMIN))
                .map(UserSummaryResponse::of)
                .toList();
    }


    @Override
    @Transactional
    public UserResponse register(UserPostRequest request) {
        assertEmailUniqueness(request.email());
        var user = UserPostRequest.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);

        var profile = ProfilePostRequest.toEntity(request.profile());
        user.setProfile(profile);

        var profileEntity = ProfilePostRequest.toEntity(request.profile());
        user.setProfile(profileEntity);
        return UserResponse.of(userRepository.save(user));
    }


    @Override
    public void sendVerificationCode(UUID id) {
        var user = getUserByIdOrThrow(id);
        if (user.isVerified()) throw new ConflictException("User is already verified");

        var key = VERIFICATION_CODE_PREFIX + user.getEmail();
        if (cachingService.exists(key))
            throw new ConflictException("Verification code already sent");


        var code = CodeGeneratorUtil.generateVerificationCode();
        cachingService.save(key, code , VERIFICATION_CODE_TTL);

        var template = EmailTemplates.confirmEmailTemplate(code);
        emailService.send(
                user.getEmail(),
                template.subject(),
                template.body()
        );
    }


    @Override
    @Transactional
    public void verify(String email, String code) {
        var key = VERIFICATION_CODE_PREFIX + email;
        if (!cachingService.isValid(key, code)) {
            throw new InvalidCredentialsException("Code invalid or expired");
        }
        var user = getUserByEmailOrThrow(email);
        user.setVerified(true);
        userRepository.save(user);
    }


    @Override
    @Transactional
    public void changePassword(UUID id, UpdatePasswordRequest request) {
        User user = getUserByIdOrThrow(id);
        assertPasswordMatch(request.currentPassword(), user.getPassword());
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void softDelete(UUID id) {
        User user = getUserByIdOrThrow(id);
        user.softDelete();
        // delete related things later (profile, parcels,trips ...)
        authService.logout(user.getId());
        userRepository.save(user);
    }


    // ----------------------------------------------------------------

    public User getUserByIdOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User getUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
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

