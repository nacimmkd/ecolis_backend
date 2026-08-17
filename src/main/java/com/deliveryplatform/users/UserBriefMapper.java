package com.deliveryplatform.users;

import com.deliveryplatform.storage.MediaUrlResolver;
import com.deliveryplatform.users.dto.UserBrief;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = {MediaUrlResolver.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserBriefMapper {

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "firstName", source = "profile.firstName")
    @Mapping(target = "lastName", source = "profile.lastName")
    @Mapping(target = "avatarUrl", source = "profile.avatarKey", qualifiedByName = "resolveUrl")
    @Mapping(target = "avgRating", source = "profile.avgRating")
    @Mapping(target = "reviewCount", source = "profile.reviewCount")
    @Mapping(target = "verified", source = "verified")
    UserBrief toRefDto(User user);
}