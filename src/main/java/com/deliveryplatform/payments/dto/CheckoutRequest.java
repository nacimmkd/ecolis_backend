package com.deliveryplatform.payments.dto;

import com.deliveryplatform.payments.Price;

import java.util.UUID;

public record CheckoutRequest(
        UUID bookingId,
        Price amount,
        String label
) {}