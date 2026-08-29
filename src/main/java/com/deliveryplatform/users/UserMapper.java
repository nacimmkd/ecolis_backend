package com.deliveryplatform.users;

import com.deliveryplatform.profiles.ProfileMapper;
import com.deliveryplatform.users.dto.UserDetails;
import com.deliveryplatform.users.dto.UserSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = {ProfileMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserMapper {

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "profile", source = "profile")
    UserDetails toDetailsDto(User user);

    @Mapping(target = "userId", source = "id")
    UserSummary toSummaryDto(User user);
}