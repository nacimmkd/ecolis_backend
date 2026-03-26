package com.deliveryplatform.profiles.dto;

import java.math.BigDecimal;

public record ProfileResponse(
        String firstName,
        String lastName,
        String phone,
        BigDecimal avgRating,
        int totalDeliveries,
        int totalOrders,
        String iban
){}