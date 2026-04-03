package com.deliveryplatform.notifications;

import java.time.LocalDate;
import java.util.UUID;

public final class NotificationPayloads {

    private NotificationPayloads() {}

    public record BookingRequested(String departureCity, String arrivalCity) {}
    public record BookingAccepted(String carrierName, double price) {}
    public record BookingRejected(String departureCity, String arrivalCity) {}
    public record BookingCancelled(UUID bookingId) {}
    public record PaymentReceived(double amount, UUID bookingId) {}
    public record PaymentFailed(double amount, UUID bookingId) {}
    public record TripCancelled(String departureCity, String arrivalCity, LocalDate departureDate) {}
    public record ParcelDelivered(UUID parcelId) {}
    public record ParcelPickedUp(UUID parcelId) {}
}
