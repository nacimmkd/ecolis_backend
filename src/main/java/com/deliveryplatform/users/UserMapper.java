package com.deliveryplatform.users;

import com.deliveryplatform.users.dto.UserDto;
import com.deliveryplatform.users.dto.UserSummaryDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
@DecoratedWith(UserMapperDecorator.class)
public interface UserMapper {

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "profile", ignore = true)
    UserDto toDto(User user);

    @Mapping(target = "userId", source = "id")
    UserSummaryDto toSummaryDto(User user);
}