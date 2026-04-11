package com.deliveryplatform.storage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StorageRepository extends JpaRepository<Image, UUID> {

    Optional<Image> findByKey(String key);
}
