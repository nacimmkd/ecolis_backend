package com.deliveryplatform.reviews;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    boolean existsByIdAndReviewerId(UUID id, UUID reviewerId);

    boolean existsByBookingIdAndReviewerId(UUID bookingId, UUID reviewerId);

    Page<Review> findByRevieweeId(UUID revieweeId, Pageable pageable);

    long countByReviewee_Id(UUID revieweeId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewee.id = :revieweeId")
    Double findAverageRatingByRevieweeId(@Param("revieweeId") UUID revieweeId);
}
