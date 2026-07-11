package com.deliveryplatform.profiles;

import com.deliveryplatform.images.ImageMapper;
import com.deliveryplatform.profiles.dto.ProfileDetails;
import com.deliveryplatform.profiles.dto.ProfileSummary;
import com.deliveryplatform.reviews.Review;
import com.deliveryplatform.reviews.ReviewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {ImageMapper.class, ReviewMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProfileMapper {

    @Mapping(target = "profileId", source = "profile.id")
    @Mapping(target = "reviews", source = "reviews")
    ProfileDetails toDetailedDto(Profile profile, List<Review> reviews);

    @Mapping(target = "profileId", source = "id")
    ProfileSummary toSummaryDto(Profile profile);
}