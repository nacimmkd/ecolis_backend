package com.deliveryplatform.profiles;

import com.deliveryplatform.profiles.dto.ProfileStatsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    @Query("""
        SELECT AVG(r.rating)
        FROM User u
        LEFT JOIN Review r
        WHERE u.id = :userId
    """)
    Double getAvgRating(@Param("userId") UUID userId);

    @Query(value = """
    SELECT
        AVG(r.rating)                                           AS avgRating,
        COUNT(DISTINCT r.id)                                    AS reviewCount,
        COUNT(DISTINCT CASE WHEN t.status = 'COMPLETED'
            THEN t.id END)                                      AS completedTrips,
        COUNT(DISTINCT CASE WHEN p.status = 'DELIVERED'
            THEN p.id END)                                      AS deliveredParcels
    FROM users u
    LEFT JOIN reviews r  ON r.reviewee_id = u.id
    LEFT JOIN trips t    ON t.user_id = u.id
    LEFT JOIN parcels p  ON p.user_id = u.id
    WHERE u.id = :userId
    GROUP BY u.id
    """, nativeQuery = true)
    ProfileStatsProjection getProfileStats(@Param("userId") UUID userId);
}
