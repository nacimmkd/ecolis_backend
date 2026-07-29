package com.deliveryplatform.transfers.accounts.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBankAccountRequest {
    @NotBlank
    private String iban;
}