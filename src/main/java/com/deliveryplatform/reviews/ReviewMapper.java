package com.deliveryplatform.reviews;

import com.deliveryplatform.reviews.dto.CreateReviewRequest;
import com.deliveryplatform.reviews.dto.ReviewResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    Review toEntity(CreateReviewRequest createReviewRequest);

    @Mapping(source = "reviewer.id", target ="reviewerId")
    @Mapping(source = "reviewee.id", target ="revieweeId")
    @Mapping(source = "booking.id", target ="bookingId")
    ReviewResponse toResponse(Review review);
}
