package com.deliveryplatform.messages;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageImageRepository extends JpaRepository<MessageImage, UUID> {
}