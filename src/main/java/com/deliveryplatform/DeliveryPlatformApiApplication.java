package com.deliveryplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class DeliveryPlatformApiApplication {

	public static void main(String[] args) {
        SpringApplication.run(DeliveryPlatformApiApplication.class, args);
    }

}
