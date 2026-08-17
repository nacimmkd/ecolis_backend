package com.deliveryplatform.parcels;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ParcelImageRepository extends JpaRepository<ParcelImage, UUID> {
}