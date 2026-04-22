package com.deliveryplatform.users;

import com.deliveryplatform.users.dto.UpdatePasswordRequest;
import com.deliveryplatform.users.dto.UserRequest;
import com.deliveryplatform.users.dto.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse findById(UUID id);
    List<UserResponse> findAll();
    UserResponse register(UserRequest request);
    void sendVerificationCode(UUID id);
    void verify(String email, String code);
    void changePassword(UUID id, UpdatePasswordRequest request);
    void softDelete(UUID id);
}
