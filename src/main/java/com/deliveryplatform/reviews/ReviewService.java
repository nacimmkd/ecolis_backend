package com.deliveryplatform.reviews;

import com.deliveryplatform.reviews.dto.CreateReviewRequest;
import com.deliveryplatform.reviews.dto.ReviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ReviewService {
    ReviewDto create(CreateReviewRequest request, UUID reviewerId);
    Page<ReviewDto> getUserReviews(UUID revieweeId, Pageable pageable);
    void remove(UUID reviewId, UUID reviewerId);
}
