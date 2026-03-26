package com.deliveryplatform.users;

import com.deliveryplatform.users.dto.UserRequest;
import com.deliveryplatform.users.dto.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toDto(User user);
    User toEntity(UserRequest request);
}
