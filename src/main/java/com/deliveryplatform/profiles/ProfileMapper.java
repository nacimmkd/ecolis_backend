package com.deliveryplatform.profiles;

import com.deliveryplatform.images.ImageMapper;
import com.deliveryplatform.profiles.dto.ProfileDetails;
import com.deliveryplatform.profiles.dto.ProfileSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(
        componentModel = "spring",
        uses = {ImageMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProfileMapper {

    @Mapping(target = "profileId", source = "id")
    ProfileDetails toDetailedDto(Profile profile);

    @Mapping(target = "profileId", source = "id")
    ProfileSummary toSummaryDto(Profile profile);
}