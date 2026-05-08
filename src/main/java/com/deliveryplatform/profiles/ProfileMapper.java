package com.deliveryplatform.profiles;


import com.deliveryplatform.profiles.dto.ProfileDetails;
import com.deliveryplatform.profiles.dto.ProfileSummary;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
@DecoratedWith(ProfileMapperDecorator.class)
public interface ProfileMapper {

    @Mapping(target = "profileId", source = "id")
    @Mapping(target = "avatarUrl", ignore = true)
    ProfileDetails toDetailedDto(Profile profile);

    @Mapping(target = "profileId", source = "id")
    @Mapping(target = "avatarUrl", ignore = true)
    ProfileSummary toSummaryDto(Profile profile);
}