package com.deliveryplatform.reviews.events;

import java.util.UUID;

public record ReviewCreatedEvent(UUID revieweeId) {
}