package com.deliveryplatform.payments.dto;

import com.deliveryplatform.payments.Payment;
import com.deliveryplatform.payments.PaymentStatus;

import java.util.UUID;


public record PaymentResponse(
        UUID paymentId,
        UUID requestId,
        long amount,
        String currency,
        PaymentStatus status,
        String clientSecret
) {

    public static PaymentResponse from(Payment payment) {
        return from(payment, null);
    }

    public static PaymentResponse from(Payment payment, String clientSecret) {
        return new PaymentResponse(
                payment.getId(),
                payment.getRequest().getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                clientSecret);
    }
}