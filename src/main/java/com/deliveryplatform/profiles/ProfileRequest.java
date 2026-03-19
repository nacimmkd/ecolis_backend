package com.deliveryplatform.profiles;

import com.deliveryplatform.validations.Iban;
import com.deliveryplatform.validations.Phone;
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
