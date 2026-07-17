package com.deliveryplatform.notifications;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {

    VERIFY_USER(false),
    USER_CREATED(false),

    MESSAGE_RECEIVED(true),

    REQUEST_RECEIVED(true),

    BOOKING_CREATED(true),
    BOOKING_CANCELED(true),
    BOOKING_COMPLETED(true),

    TRIP_CANCELLED(true),

    PARCEL_DELIVERED(true);

    private boolean persistent;
}