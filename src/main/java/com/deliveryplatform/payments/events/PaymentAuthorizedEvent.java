package com.deliveryplatform.payments.events;

import java.util.UUID;

public record PaymentAuthorizedEvent(UUID requestId) {
}
