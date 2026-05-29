package com.deliveryplatform.reviews;

import com.deliveryplatform.reviews.dto.CreateReviewRequest;
import com.deliveryplatform.reviews.dto.ReviewDto;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    ReviewDto create(CreateReviewRequest request, UUID reviewerId);
    List<ReviewDto> getUserReviews(UUID revieweeId);
    void remove(UUID reviewId, UUID reviewerId);
}
