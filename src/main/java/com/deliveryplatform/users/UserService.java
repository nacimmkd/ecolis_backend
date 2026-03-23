package com.deliveryplatform.users;

import com.deliveryplatform.profiles.Profile;
import com.deliveryplatform.profiles.ProfileMapper;
import com.deliveryplatform.users.exceptions.EmailAlreadyExistsException;
import com.deliveryplatform.users.exceptions.PasswordNotValidException;
import com.deliveryplatform.users.exceptions.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.deliveryplatform.users.UserDto.*;
import com.deliveryplatform.profiles.ProfileDto.*;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper      userMapper;
    private final ProfileMapper profileMapper;



    public UserResponse findById(UUID id) {
        var user = getUserByIdOrThrow(id);
        return userMapper.toDto(user);
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .filter(user -> !user.getRole().equals(Role.ADMIN))
                .map(userMapper::toDto)
                .toList();
    }


    @Transactional
    public UserResponse register(UserRequest request) {
        checkEmailUniquenessOrThrow(request.email());
        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        user.setProfile(profileMapper.toEntity(request.profile()));
        userRepository.save(user);
        return userMapper.toDto(user);
    }


    @Transactional
    public UserResponse updateProfile(UUID id, ProfileRequest request) {
        User user = getUserByIdOrThrow(id);
        updateProfileFields(user.getProfile(), request);
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public void changePassword(UUID id, ChangePasswordRequest request) {
        User user = getUserByIdOrThrow(id);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new PasswordNotValidException();
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void softDelete(UUID id) {
        User user = getUserByIdOrThrow(id);
        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional
    public void banUser(UUID id) {
        User user = getUserByIdOrThrow(id);
        user.setActive(false);
        userRepository.save(user);
    }

    // ----------------------------------------------------------------

    public User getUserByIdOrThrow(UUID id) {
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

