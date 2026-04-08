package com.deliveryplatform.reviews;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    boolean existsByIdAndReviewerId(UUID id, UUID reviewerId);
    boolean existsByBookingIdAndReviewerId(UUID bookingId, UUID reviewerId);
    Optional<Review> findByRevieweeId(UUID revieweeId);
}
