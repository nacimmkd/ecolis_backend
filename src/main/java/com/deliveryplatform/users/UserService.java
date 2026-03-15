package com.deliveryplatform.users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper      userMapper;
    private final ProfileMapper   profileMapper;


    @PreAuthorize("hasRole('ADMIN')")
    public UserDto findById(UUID id) {
        var user = getUserOrThrow(id);
        return userMapper.toDto(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .filter(user -> !user.getRole().equals(Role.ADMIN))
                .map(userMapper::toDto)
                .toList();
    }


    @Transactional
    public UserDto register(RegisterUserRequest request) {
        checkEmailUniquenessOrThrow(request.email());
        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        user.setProfile(profileMapper.toEntity(request.profile()));
        userRepository.save(user);
        return userMapper.toDto(user);
    }


    @Transactional
    public UserDto updateProfile(UUID id, ProfileRequest request) {
        User user = getUserOrThrow(id);
        updateProfileFields(user.getProfile(), request);
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public void changePassword(UUID id, ChangePasswordRequest request) {
        User user = getUserOrThrow(id);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new PasswordNotValidException();
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void softDelete(UUID id) {
        User user = getUserOrThrow(id);
        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void banUser(UUID id) {
        User user = getUserOrThrow(id);
        user.setActive(false);
        userRepository.save(user);
    }

    // ----------------------------------------------------------------

    public User getUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    private void checkEmailUniquenessOrThrow(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    throw new EmailAlreadyExistsException();
                });
    }

    private void updateProfileFields(Profile profile, ProfileRequest request) {
        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        profile.setPhone(request.phone());
        profile.setIban(request.iban());
    }
}

