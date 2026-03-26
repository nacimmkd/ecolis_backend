package com.deliveryplatform.profiles.dto;

import com.deliveryplatform.common.validations.Phone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileRequest(
        @NotBlank(message = "Invalid firstname") @Size(max = 100)
        String firstName,

        @NotBlank(message = "Invalid lastname") @Size(max = 100)
        String lastName,

        @Phone
        String phone,

        //@Iban // desactivated for testing
        @NotBlank
        String iban
) {}
