package com.deliveryplatform.notifications;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class NotificationPayloads {

    private NotificationPayloads() {}

    public record BookingRequested(String departureCity, String arrivalCity) {}
    public record BookingAccepted(String carrierName, BigDecimal price) {}
    public record BookingRejected(String departureCity, String arrivalCity) {}
    public record BookingCancelled(UUID bookingId) {}
    public record PaymentReceived(BigDecimal amount, UUID bookingId) {}
    public record PaymentFailed(BigDecimal amount, UUID bookingId) {}
    public record TripCancelled(String departureCity, String arrivalCity, LocalDate departureDate) {}
    public record ParcelDelivered(UUID parcelId) {}
    public record ParcelPickedUp(UUID parcelId) {}
}
