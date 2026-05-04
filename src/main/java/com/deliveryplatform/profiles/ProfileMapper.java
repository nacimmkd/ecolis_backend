package com.deliveryplatform.profiles;

import com.deliveryplatform.profiles.dto.ProfileResponse;
import com.deliveryplatform.profiles.dto.ProfileSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(target = "profileId", source = "id")
    @Mapping(target = "avatarUrl", ignore = true)
    ProfileResponse toResponse(Profile profile);

    @Mapping(target = "profileId", source = "id")
    @Mapping(target = "avatarUrl", ignore = true)
    ProfileSummary toSummaryResponse(Profile profile);
}
