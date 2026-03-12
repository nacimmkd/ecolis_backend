package com.deliveryplatform.users;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toResponse(User user);
}
