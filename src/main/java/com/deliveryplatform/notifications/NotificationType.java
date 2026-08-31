package com.deliveryplatform.notifications;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {

    VERIFY_USER(false),
    RESET_PASSWORD(false),
    USER_CREATED(true),

    MESSAGE_RECEIVED(false),

    REQUEST_RECEIVED(true),

    BOOKING_CREATED(true),
    BOOKING_CANCELED(true),
    BOOKING_COMPLETED(true),
    BOOKING_PAID(true),

    TRIP_CANCELLED(true),

    PARCEL_DELIVERED(true),

    REVIEW_CREATED(true);

    private boolean persistent;
}