package com.deliveryplatform.requests.events;

import java.util.UUID;

public record RequestAcceptedEvent(UUID requestId) {
}