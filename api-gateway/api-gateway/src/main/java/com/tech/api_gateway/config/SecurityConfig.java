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
                .pathMatchers(HttpMethod.GET, "/product-service/api/product/**").hasAnyRole("CUSTOMER", "CATALOG_MANAGER")
                .pathMatchers(HttpMethod.PUT, "/product-service/api/product/**").hasAnyRole("CATALOG_MANAGER")
             
                //inventory-service paths
                .pathMatchers(HttpMethod.GET, "/inventory-service/api/inventory/check/**").hasAnyRole("INVENTORY_MANAGER")
                .pathMatchers(HttpMethod.PUT, "/inventory-service/api/inventory/**").hasAnyRole("INVENTORY_MANAGER")
                
                //order-service-paths
                .pathMatchers(HttpMethod.POST, "/order-service/api/order/**").hasRole("CUSTOMER")
                .pathMatchers(HttpMethod.GET, "/order-service/api/order/myorders").hasRole("CUSTOMER")
                .pathMatchers(HttpMethod.POST, "/order-service/cart/**").hasRole("CUSTOMER")
                .pathMatchers(HttpMethod.GET, "/order-service/cart/**").hasRole("CUSTOMER")
                
                //payment-service-paths
                .pathMatchers(HttpMethod.POST, "/payment-service/api/pay/**").hasRole("CUSTOMER")

                //shipping-servic-paths
                .pathMatchers(HttpMethod.POST, "/shipping-service/shipping/ship/**").hasRole("SHIPPING_STAFF")
                .pathMatchers(HttpMethod.GET, "/shipping-service/shipping/**").hasRole("SHIPPING_STAFF")
           
                
                .anyExchange().authenticated()
            )
            .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }
}
