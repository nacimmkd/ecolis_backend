package com.deliveryplatform.users;

import com.deliveryplatform.users.dto.UpdatePasswordRequest;
import com.deliveryplatform.users.dto.UserCreateRequest;
import com.deliveryplatform.users.dto.UserDetails;
import com.deliveryplatform.users.dto.UserSummary;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDetails findById(UUID id);

    List<UserSummary> findAll();

    UserDetails register(UserCreateRequest request);

    void sendVerificationCode(String email);

    void verify(String email, String code);

    void updatePassword(UUID id, UpdatePasswordRequest request);

    void softDelete(UUID id);
}
