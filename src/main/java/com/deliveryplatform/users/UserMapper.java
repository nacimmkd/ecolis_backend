package com.deliveryplatform.users;

import com.deliveryplatform.images.ImageMapper;
import com.deliveryplatform.profiles.ProfileMapper;
import com.deliveryplatform.users.dto.UserDetails;
import com.deliveryplatform.users.dto.UserBrief;
import com.deliveryplatform.users.dto.UserSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = {ProfileMapper.class, ImageMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserMapper {

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "profile", source = "profile")
    UserDetails toDetailsDto(User user);


    @Mapping(target = "userId", source = "id")
    UserSummary toSummaryDto(User user);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "firstName", source = "profile.firstName")
    @Mapping(target = "lastName", source = "profile.lastName")
    @Mapping(target = "avatar", source = "profile.avatar")
    @Mapping(target = "avgRating", source = "profile.avgRating")
    @Mapping(target = "reviewCount", source = "profile.reviewCount")
    @Mapping(target = "verified", source = "verified")
    UserBrief toRefDto(User user);
}