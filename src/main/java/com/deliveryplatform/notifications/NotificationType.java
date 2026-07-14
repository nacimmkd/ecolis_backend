package com.deliveryplatform.notifications;

import lombok.Getter;

@Getter
public enum NotificationType {

    VERIFY_USER,
    USER_CREATED,

    MESSAGE_RECEIVED,

    NEW_REQUEST_RECEIVED,

    BOOKING_CREATED,
    BOOKING_CANCELED,

    TRIP_CANCELLED,

    PARCEL_DELIVERED
}