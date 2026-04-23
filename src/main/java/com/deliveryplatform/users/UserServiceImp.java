package com.deliveryplatform.users;

import com.deliveryplatform.auth.jwt.RefreshTokenService;
import com.deliveryplatform.common.CodeGeneratorUtil;
import com.deliveryplatform.common.exceptions.ConflictException;
import com.deliveryplatform.common.exceptions.InvalidCredentialsException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.emails.EmailService;
import com.deliveryplatform.emails.EmailTemplates;
import com.deliveryplatform.profiles.dto.ProfileRequest;
import com.deliveryplatform.users.dto.UpdatePasswordRequest;
import com.deliveryplatform.users.dto.UserRequest;
import com.deliveryplatform.users.dto.UserResponse;
import com.deliveryplatform.users.dto.UserSummaryResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserVerificationService userVerificationService;
    private final RefreshTokenService refreshTokenService;


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
    public UserResponse register(UserRequest request) {
        assertEmailUniqueness(request.email());
        var user = UserRequest.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);

        var profile = ProfileRequest.toEntity(request.profile());
        user.setProfile(profile);

        var profileEntity = ProfileRequest.toEntity(request.profile());
        user.setProfile(profileEntity);
        return UserResponse.of(userRepository.save(user));
    }


    @Override
    public void sendVerificationCode(UUID id) {
        var user = getUserByIdOrThrow(id);
        if (user.isVerified()) throw new ConflictException("User is already verified");
        if (userVerificationService.exists(user.getEmail()))
            throw new ConflictException("Verification code already sent");


        var code = CodeGeneratorUtil.generateVerificationCode();
        userVerificationService.send(user.getEmail(), code);

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
        if (!userVerificationService.verify(email, code)) {
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

        user.setDeleted(true);
        user.setEmail("_deleted_" + UUID.randomUUID() + "_" + user.getEmail());
        user.setDeletedAt(OffsetDateTime.now());

        refreshTokenService.remove(user.getId());
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

