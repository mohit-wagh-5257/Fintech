package com.fintech.payment_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient; // Use the Spring version

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        // No casting to Object! 
        return builder
                .baseUrl("http://localhost:8082") 
                .build();
    }
}