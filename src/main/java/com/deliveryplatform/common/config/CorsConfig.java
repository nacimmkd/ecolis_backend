package com.deliveryplatform.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "cors")
public class CorsConfig {
    private List<String> allowedOrigins = List.of("*");
}
