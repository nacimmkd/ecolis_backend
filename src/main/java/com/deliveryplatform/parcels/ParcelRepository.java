package com.deliveryplatform.parcels;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ParcelRepository extends JpaRepository<Parcel, UUID> {

    List<Parcel> findByUserId(UUID userId);
    List<Parcel> findByStatus(ParcelStatus status);

}
