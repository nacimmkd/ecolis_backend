package com.deliveryplatform.reviews;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    boolean existsByIdAndReviewerId(UUID id, UUID reviewerId);
    boolean existsByBookingIdAndReviewerId(UUID bookingId, UUID reviewerId);
    List<Review> findByRevieweeId(UUID revieweeId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewee.id = :userId")
    BigDecimal calculateAvgRating(UUID userId);
}
