package com.deliveryplatform.payments.dto;

import com.deliveryplatform.payments.Price;
import com.deliveryplatform.payments.Payment;
import com.deliveryplatform.payments.PaymentStatus;

import java.util.UUID;


public record PaymentResponse(
        UUID paymentId,
        UUID bookingId,
        Price amount,
        PaymentStatus status,
        String clientSecret
) {

    public static PaymentResponse from(Payment payment) {
        return from(payment, null);
    }

    public static PaymentResponse from(Payment payment, String clientSecret) {
        return new PaymentResponse(
                payment.getId(),
                payment.getBooking().getId(),
                payment.getPrice(),
                payment.getStatus(),
                clientSecret);
    }
}