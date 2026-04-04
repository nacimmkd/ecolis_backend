package com.deliveryplatform.notifications;

import lombok.Getter;

@Getter
public enum NotificationType {

    BOOKING_REQUESTED,
    BOOKING_ACCEPTED,
    BOOKING_REJECTED,
    BOOKING_CANCELLED,

    PAYMENT_RECEIVED,
    PAYMENT_FAILED,

    TRIP_STARTED,
    TRIP_COMPLETED,
    TRIP_CANCELLED,

    PARCEL_PICKED_UP,
    PARCEL_DELIVERED;
}