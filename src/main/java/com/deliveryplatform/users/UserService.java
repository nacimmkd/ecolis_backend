package com.deliveryplatform.users;

import com.deliveryplatform.users.dto.UpdatePasswordRequest;
import com.deliveryplatform.users.dto.UserPostRequest;
import com.deliveryplatform.users.dto.UserDto;
import com.deliveryplatform.users.dto.UserSummaryDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto findById(UUID id);

    List<UserSummaryDto> findAll();

    UserDto register(UserPostRequest request);

    void sendVerificationCode(UUID id);

    void verify(String email, String code);

    void changePassword(UUID id, UpdatePasswordRequest request);

    void softDelete(UUID id);
}
