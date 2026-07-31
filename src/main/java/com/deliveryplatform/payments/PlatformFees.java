package com.deliveryplatform.payments;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "platform")
public class PlatformFees {
    private long constFee;
    private long feeRate;
    private String currency;
}
