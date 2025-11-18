package com.tech.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; 
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTFilter jwtFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

            .authorizeExchange(exchange -> exchange
            		
            	//product-service paths
                .pathMatchers(HttpMethod.POST, "/product-service/api/product/**").hasRole("PRODUCT_ADMIN")
                .pathMatchers(HttpMethod.GET, "/product-service/api/product/**").hasAnyRole("CUSTOMER", "PRODUCT_ADMIN")
             
                //inventory-service paths
                .pathMatchers(HttpMethod.GET, "/inventory-service/api/inventory/*").hasAnyRole("INVENTORY_MANAGER","PRODUCT_ADMIN")
                .pathMatchers(HttpMethod.GET, "/inventory-service/api/inventory/check/*").hasAnyRole("INVENTORY_MANAGER","CUSTOMER")
                .pathMatchers(HttpMethod.PUT, "/inventory-service/api/inventory/increase/*").hasRole("INVENTORY_MANAGER")
                
                //order-service-paths
                .pathMatchers(HttpMethod.POST, "/order-service/api/order").hasRole("CUSTOMER")
                
                //aggregated response 
                .pathMatchers(HttpMethod.GET,"/product-info/{skuCode}").hasRole("PRODUCT_ADMIN")

                .anyExchange().authenticated()
            )
            .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }
}
