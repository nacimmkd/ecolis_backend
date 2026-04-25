package com.deliveryplatform.parcels;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParcelRepository extends JpaRepository<Parcel, UUID> {

    @Query("SELECT p FROM Parcel p WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
    List<Parcel> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT p FROM Parcel p LEFT JOIN FETCH p.thumbnailImage LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Parcel> findParcelWithImagesById(@Param("id") UUID id);
}
