package com.deliveryplatform.users;

import org.mapstruct.Mapper;
import com.deliveryplatform.users.UserDto.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toDto(User user);
    User toEntity(UserRequest request);
}
