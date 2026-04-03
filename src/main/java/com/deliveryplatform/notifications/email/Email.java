package com.deliveryplatform.notifications.email;

import com.deliveryplatform.notifications.NotificationPayloads;
import com.deliveryplatform.notifications.NotificationType;
import lombok.Getter;

@Getter
public final class Email {

    private final String to;
    private final String subject;
    private final String body;

    private Email(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    public static Email create(String to, NotificationType type, Object payload) {
        return new Email(to, type.getSubject(), buildBody(type, payload));
    }

    private static String buildBody(NotificationType type, Object payload) {
        return switch (type) {
            case BOOKING_REQUESTED -> EmailTemplates.bookingRequested(
                    (NotificationPayloads.BookingRequested) payload);
            case BOOKING_ACCEPTED  -> EmailTemplates.bookingAccepted(
                    (NotificationPayloads.BookingAccepted) payload);
            case BOOKING_REJECTED  -> EmailTemplates.bookingRejected(
                    (NotificationPayloads.BookingRejected) payload);
            case BOOKING_CANCELLED -> EmailTemplates.bookingCancelled(
                    (NotificationPayloads.BookingCancelled) payload);
            case PAYMENT_RECEIVED  -> EmailTemplates.paymentReceived(
                    (NotificationPayloads.PaymentReceived) payload);
            case PAYMENT_FAILED    -> EmailTemplates.paymentFailed(
                    (NotificationPayloads.PaymentFailed) payload);
            case TRIP_CANCELLED    -> EmailTemplates.tripCancelled(
                    (NotificationPayloads.TripCancelled) payload);
            case PARCEL_DELIVERED  -> EmailTemplates.parcelDelivered(
                    (NotificationPayloads.ParcelDelivered) payload);
            default -> "";
        };
    }
}