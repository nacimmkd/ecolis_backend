package com.deliveryplatform.profiles;

import com.deliveryplatform.profiles.dto.ProfileDto;
import com.deliveryplatform.reviews.ReviewMapper;
import com.deliveryplatform.storage.MediaUrlResolver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = {MediaUrlResolver.class, ReviewMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProfileMapper {

    @Mapping(target = "profileId", source = "profile.id")
    @Mapping(target = "avatarUrl", source = "profile.avatarKey", qualifiedByName = "resolveUrl")
    @Mapping(target = "phoneVisible", constant = "true")
    ProfileDto toDetailedDto(Profile profile);

}