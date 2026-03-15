package com.deliveryplatform.users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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


    public UserDto findById(UUID id) {
        var user = getUserOrThrow(id);
        return userMapper.toDto(user);
    }


    public List<User> findAll() {
        return userRepository.findAll();
    }


    @Transactional
    public UserDto register(RegisterUserRequest request) {

        var user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

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
    public void banUser(UUID id) {
        User user = getUserOrThrow(id);
        user.setActive(false);
        userRepository.save(user);
    }

    // ----------------------------------------------------------------

    private User getUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    private void updateProfileFields(Profile profile, ProfileRequest request) {
        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        profile.setPhone(request.phone());
        profile.setIban(request.iban());
    }
}

