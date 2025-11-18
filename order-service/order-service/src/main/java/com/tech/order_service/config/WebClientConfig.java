package com.tech.order_service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/*
@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder productWebClientBuilder() {
        return WebClient.builder()
                .baseUrl("http://product-service"); // 
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder inventoryWebClientBuilder() {
        return WebClient.builder()
                .baseUrl("http://inventory-service"); // 
    }
}
*/