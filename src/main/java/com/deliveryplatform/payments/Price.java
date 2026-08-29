package com.deliveryplatform.payments;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Price {

    @Positive(message = "price must be positive")
    @Min(value = 50, message = "must be more than 50 cents")
    @Max(value = 600, message = "price must be under 600 cents")
    private long amountInCents;

    @NotBlank
    private String currency;

    private Price(long amountInCents, String currency) {
        this.amountInCents = amountInCents;
        this.currency = currency;
    }

    public static Price of(long amountInCents, String currency) {
        return new Price(amountInCents, currency);
    }

    public static Price zero(String currency) {
        return new Price(0L, currency);
    }

    public Price add(Price other) {
        assertSameCurrency(other);
        return new Price(this.amountInCents + other.amountInCents, currency);
    }

    public Price subtract(Price other) {
        assertSameCurrency(other);
        return new Price(this.amountInCents - other.amountInCents, currency);
    }

    public Price multiply(BigDecimal factor) {
        long result = BigDecimal.valueOf(amountInCents)
                .multiply(factor)
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
        return new Price(result, currency);
    }

    public BigDecimal toAmount() {
        return BigDecimal.valueOf(amountInCents, 2);
    }

    private void assertSameCurrency(Price other) {
        if (!this.currency.equalsIgnoreCase(other.currency))
            throw new IllegalArgumentException(
                    "Currency mismatch: %s vs %s".formatted(this.currency, other.currency));
    }
}
