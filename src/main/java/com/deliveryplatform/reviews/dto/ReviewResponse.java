package com.deliveryplatform.reviews.dto;

import com.deliveryplatform.reviews.Review;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record ReviewResponse(
        UUID id,
        UUID reviewerId,
        UUID revieweeId,
        UUID bookingId,
        Short rating,
        String comment,
        OffsetDateTime createdAt
) {

    public static ReviewResponse of(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .reviewerId(review.getReviewer().getId())
                .revieweeId(review.getReviewee().getId())
                .bookingId(review.getBooking().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
