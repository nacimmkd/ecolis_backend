package com.deliveryplatform.users;

import com.deliveryplatform.users.dto.UserResponse;
import com.deliveryplatform.users.dto.UserSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "profile", ignore = true)
    UserResponse toResponse(User user);

    @Mapping(target = "userId", source = "id")
    UserSummaryResponse toSummaryResponse(User user);
}
