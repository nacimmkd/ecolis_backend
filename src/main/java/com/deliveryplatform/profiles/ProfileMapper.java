package com.deliveryplatform.profiles;

import com.deliveryplatform.profiles.dto.ProfileDetails;
import com.deliveryplatform.profiles.dto.ProfileSummary;
import com.deliveryplatform.reviews.Review;
import com.deliveryplatform.reviews.ReviewMapper;
import com.deliveryplatform.storage.MediaUrlResolver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {MediaUrlResolver.class, ReviewMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProfileMapper {

    @Mapping(target = "profileId", source = "profile.id")
    @Mapping(target = "avatarUrl", source = "profile.avatarKey", qualifiedByName = "resolveUrl")
    ProfileDetails toDetailedDto(Profile profile);

    @Mapping(target = "profileId", source = "id")
    @Mapping(target = "avatarUrl", source = "avatarKey", qualifiedByName = "resolveUrl")
    ProfileSummary toSummaryDto(Profile profile);
}