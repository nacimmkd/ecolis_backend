package com.deliveryplatform.addresses.nominatim;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class NominatimConfig {

    private final NominatimProperties nominatimProperties;

    @Bean
    public WebClient nominatimWebClient(){
        return WebClient.builder()
                .baseUrl(nominatimProperties.getBaseUrl())
                .defaultHeader("User-Agent",nominatimProperties.getUserAgent())
                .defaultHeader("Accept-Language", "fr")
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(1024 * 1024)) // 1 MB buffer
                .build();
    }
}
