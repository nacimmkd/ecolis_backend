package com.deliveryplatform.trips.events;

import java.util.UUID;

public record TripCompletedEvent(UUID tripId, UUID ownerId) {
}