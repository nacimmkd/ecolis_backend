package com.deliveryplatform.users;

import com.deliveryplatform.auth.AuthService;
import com.deliveryplatform.caching.CachingService;
import com.deliveryplatform.common.CodeGeneratorUtil;
import com.deliveryplatform.common.exceptions.ConflictException;
import com.deliveryplatform.common.exceptions.InvalidCredentialsException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.notifications.emails.EmailService;
import com.deliveryplatform.notifications.emails.Templates;
import com.deliveryplatform.profiles.Profile;
import com.deliveryplatform.users.dto.UpdatePasswordRequest;
import com.deliveryplatform.users.dto.UserCreateRequest;
import com.deliveryplatform.users.dto.UserDetails;
import com.deliveryplatform.users.dto.UserSummary;
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
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final CachingService cachingService;
    private final AuthService authService;

    private static final String VERIFICATION_CODE_PREFIX = "email:verify:";
    private static final Duration VERIFICATION_CODE_TTL = Duration.ofMinutes(5);



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

        var user = buildUser(request);
        var profile = Profile.createFromRequest(request.profile());
        user.setProfile(profile);
        return userMapper.toDetailsDto(userRepository.save(user));
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

        var template = Templates.confirmEmailTemplate(code);
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

    private User buildUser(UserCreateRequest request) {
        return User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .isVerified(false)
                .deleted(false)
                .deletedAt(null)
                .build();
    }




}

