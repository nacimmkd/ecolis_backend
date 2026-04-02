package com.deliveryplatform.notifications;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId);
    Long countByUserIdAndIsReadFalse(UUID userId);
    Optional<Notification> findByIdAndUserId(UUID id, UUID userId);
}
