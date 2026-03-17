package com.deliveryplatform.profiles;

import com.deliveryplatform.common.validations.Iban;
import com.deliveryplatform.common.validations.Phone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileRequest(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @Phone String phone,
        @Iban String iban
) {}
