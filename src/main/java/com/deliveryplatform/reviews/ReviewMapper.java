package com.deliveryplatform.reviews;

import com.deliveryplatform.reviews.dto.ReviewDto;
import com.deliveryplatform.users.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ReviewMapper {

    @Mapping(target = "reviewer", source = "reviewer")
    @Mapping(target = "reviewed", source = "reviewee")
    ReviewDto toDto(Review review);

    List<ReviewDto> toDto(List<Review> reviews);
}