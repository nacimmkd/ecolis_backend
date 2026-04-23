package com.deliveryplatform.users;

import com.deliveryplatform.users.dto.UpdatePasswordRequest;
import com.deliveryplatform.users.dto.UserPostRequest;
import com.deliveryplatform.users.dto.UserResponse;
import com.deliveryplatform.users.dto.UserSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse findById(UUID id);

    List<UserSummaryResponse> findAll();

    UserResponse register(UserPostRequest request);

    void sendVerificationCode(UUID id);

    void verify(String email, String code);

    void changePassword(UUID id, UpdatePasswordRequest request);

    void softDelete(UUID id);
}
