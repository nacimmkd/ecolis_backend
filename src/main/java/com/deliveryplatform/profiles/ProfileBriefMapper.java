package com.deliveryplatform.profiles;

import com.deliveryplatform.storage.MediaUrlResolver;
import com.deliveryplatform.users.User;
import com.deliveryplatform.profiles.dto.ProfileBrief;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = {MediaUrlResolver.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProfileBriefMapper {

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "firstName", source = "profile.firstName")
    @Mapping(target = "lastName", source = "profile.lastName")
    @Mapping(target = "avatarUrl", source = "profile.avatarKey", qualifiedByName = "resolveUrl")
    @Mapping(target = "avgRating", source = "profile.avgRating")
    @Mapping(target = "reviewCount", source = "profile.reviewCount")
    @Mapping(target = "verified", source = "profile.verified")
    ProfileBrief toRefDto(User user);
}