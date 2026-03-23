package com.deliveryplatform.common.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IbanValidator implements ConstraintValidator<Iban, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null) return true;

        // remove spaces
        String iban = value.replaceAll("\\s", "").toUpperCase();

        // check length
        if (iban.length() < 15 || iban.length() > 34) return false;

        // check first 2 letters
        if (!iban.matches("[A-Z]{2}[0-9]{2}[A-Z0-9]+")) return false;

        // MOD97 algo
        String rearranged = iban.substring(4) + iban.substring(0, 4);
        StringBuilder numericIban = new StringBuilder();
        for (char c : rearranged.toCharArray()) {
            if (Character.isLetter(c)) {
                numericIban.append(c - 'A' + 10);
            } else {
                numericIban.append(c);
            }
        }
        return mod97(numericIban.toString()) == 1;
    }

    private int mod97(String numericIban) {
        int remainder = 0;
        for (char c : numericIban.toCharArray()) {
            remainder = (remainder * 10 + (c - '0')) % 97;
        }
        return remainder;
    }
}