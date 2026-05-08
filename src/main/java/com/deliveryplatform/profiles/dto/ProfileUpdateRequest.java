package com.deliveryplatform.profiles.dto;

import com.deliveryplatform.common.validations.Phone;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @Size(max = 100)
        String firstName,

        @Size(max = 100)
        String lastName,

        @Phone
        String phone
) {}
