package com.deliveryplatform.notifications;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {

    VERIFY_USER(true),
    RESET_PASSWORD(true),
    USER_CREATED(false),

    MESSAGE_RECEIVED(true),

    REQUEST_RECEIVED(true),

    BOOKING_CREATED(true),
    BOOKING_CANCELED(true),
    BOOKING_COMPLETED(true),
    BOOKING_PAID(true),

    TRIP_CANCELLED(true),

    PARCEL_DELIVERED(true);

    private boolean persistent;
}