
package com.deliveryplatform.parcels;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParcelRepository extends JpaRepository<Parcel, UUID> {

    @EntityGraph(attributePaths = {"owner", "owner.profile", "images"})
    @Query("SELECT p FROM Parcel p WHERE p.owner.id = :userId AND (:state IS NULL OR p.state = :state)")
    Page<Parcel> findByOwnerIdAndState(@Param("userId") UUID userId, @Param("state") ParcelState state, Pageable pageable);

    @EntityGraph(attributePaths = {"owner", "owner.profile", "images"})
    @Query("SELECT p FROM Parcel p WHERE p.owner.id = :userId")
    Page<Parcel> findByOwnerId(@Param("userId") UUID userId, Pageable pageable);

    @EntityGraph(attributePaths = {"owner", "owner.profile", "trackEvents"})
    Optional<Parcel> findParcelWithTrackingById(UUID id);

    @EntityGraph(attributePaths = {"owner", "owner.profile", "images"})
    Optional<Parcel> findParcelDetailsById(UUID id);

    @EntityGraph(attributePaths = {"owner", "images"})
    Optional<Parcel> findParcelSummaryById(UUID id);

    @EntityGraph(attributePaths = {"owner", "owner.profile", "images"})
    Page<Parcel> findAll(Pageable pageable);

}