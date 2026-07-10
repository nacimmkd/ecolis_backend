
package com.deliveryplatform.parcels;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParcelRepository extends JpaRepository<Parcel, UUID> {

    @EntityGraph(attributePaths = {"owner", "owner.profile", "thumbnail", "images"})
    @Query("SELECT p FROM Parcel p WHERE p.owner.id = :userId ORDER BY p.createdAt DESC")
    List<Parcel> findByOwnerId(@Param("userId") UUID userId);

    @EntityGraph(attributePaths = {"owner", "owner.profile", "thumbnail", "trackEvents"})
    Optional<Parcel> findParcelWithTrackingById(UUID id);

    @EntityGraph(attributePaths = {"owner", "owner.profile", "thumbnail", "images"})
    Optional<Parcel> findParcelDetailsById(UUID id);

    @EntityGraph(attributePaths = {"owner", "thumbnail"})
    Optional<Parcel> findParcelSummaryById(UUID id);

}