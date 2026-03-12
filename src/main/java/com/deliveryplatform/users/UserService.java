package com.deliveryplatform.users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository  userRepository;
    private final UserMapper      userMapper;


    public UserDto findById(UUID id) {
        return userMapper.toResponse(getUserOrThrow(id));
    }

    public Page<UserDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }


    @Transactional
    public UserDto updateUser(UUID id, UpdateUserRequest request) {
        User user = getUserOrThrow(id);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhone(request.phone());
        user.setAvatarUrl(request.avatarUrl());
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public void changePassword(UUID id, ChangePasswordRequest request) {
        User user = getUserOrThrow(id);

//        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
//            throw new PasswordNotValidException();
//        }
//
//        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void softDelete(UUID id) {
        User user = getUserOrThrow(id);
        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional
    public void verifyUser(UUID id) {
        User user = getUserOrThrow(id);
        user.setVerified(true);
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
}

