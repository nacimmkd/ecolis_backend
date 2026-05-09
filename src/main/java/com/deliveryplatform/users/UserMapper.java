package com.deliveryplatform.users;

import com.deliveryplatform.profiles.ProfileMapper;
import com.deliveryplatform.users.dto.UserDto;
import com.deliveryplatform.users.dto.UserSummaryDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = ProfileMapper.class
)
public interface UserMapper {

    @Mapping(target = "userId", source = "id")
    UserDto toDto(User user);

    @Mapping(target = "userId", source = "id")
    UserSummaryDto toSummaryDto(User user);
}