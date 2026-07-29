package com.deliveryplatform.payments;

import com.stripe.StripeClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Bean
    public StripeClient stripeClient(@Value("${stripe.secret-key}") String secretKey) {
        return StripeClient.builder()
                .setApiKey(secretKey)
                .setMaxNetworkRetries(2)
                .build();
    }
}