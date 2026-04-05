package com.deliveryplatform.addresses;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "geocoding.nominatim")
public class NominatimProperties {
    private String baseUrl;
    private String userAgent;
}
