package com.deliveryplatform.profiles;

import java.math.BigDecimal;

public record ProfileDto(
        String firstName,
        String lastName,
        String phone,
        BigDecimal avgRating,
        int totalDeliveries,
        int totalOrders,
        String iban
) {
}
