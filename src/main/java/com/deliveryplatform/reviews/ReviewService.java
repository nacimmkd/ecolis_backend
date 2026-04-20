package com.deliveryplatform.reviews;

import com.deliveryplatform.reviews.dto.CreateReviewRequest;
import com.deliveryplatform.reviews.dto.ReviewResponse;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    ReviewResponse create(CreateReviewRequest request, UUID reviewerId);
    List<ReviewResponse> getUserReviews(UUID reviewerId);
    void remove(UUID reviewId, UUID reviewerId);
}
