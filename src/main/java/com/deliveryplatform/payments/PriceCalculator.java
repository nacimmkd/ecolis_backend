package com.deliveryplatform.payments;

import com.deliveryplatform.requests.Request;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PriceCalculator {

    private final PlatformFees platformFees;

    public record Price(long amount, long platformFees, String currency){};

    public Price calculate(Request request) {
        var requestPrice = request.getPriceInCents();
        var applicationFees = ( ( requestPrice * platformFees.getFeeRate() ) / 100 ) + platformFees.getConstFee();
        var amount = requestPrice + applicationFees;
        return new Price(amount, applicationFees, platformFees.getCurrency());
    }

}
