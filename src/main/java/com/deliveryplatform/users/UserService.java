package com.deliveryplatform.users;

import com.deliveryplatform.users.dto.UpdatePasswordRequest;
import com.deliveryplatform.users.dto.UserPostRequest;
import com.deliveryplatform.users.dto.UserDetails;
import com.deliveryplatform.users.dto.UserSummary;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDetails findById(UUID id);

    List<UserSummary> findAll();

    UserDetails register(UserPostRequest request);

    void sendVerificationCode(UUID id);

    void verify(String email, String code);

    void changePassword(UUID id, UpdatePasswordRequest request);

    void softDelete(UUID id);
}
