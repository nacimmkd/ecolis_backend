package com.deliveryplatform.reviews;

import com.deliveryplatform.reviews.dto.ReviewDto;
import com.deliveryplatform.profiles.ProfileBriefMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {ProfileBriefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ReviewMapper {

    @Mapping(target = "reviewer", source = "reviewer")
    ReviewDto toDto(Review review);

    List<ReviewDto> toDto(List<Review> reviews);
}