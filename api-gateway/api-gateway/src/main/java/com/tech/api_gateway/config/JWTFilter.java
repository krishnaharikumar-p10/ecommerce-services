package com.tech.api_gateway.config;

import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.tech.api_gateway.service.JWTService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class JWTFilter implements WebFilter {

    private final Logger log = LoggerFactory.getLogger(JWTFilter.class);
    private final JWTService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

       
        if (exchange.getAttributes().containsKey("jwtFilterExecuted")) {
            return chain.filter(exchange);
        }
        exchange.getAttributes().put("jwtFilterExecuted", true);

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        UsernamePasswordAuthenticationToken auth = null;
        ServerWebExchange mutatedExchange = exchange;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.info("Pre-filter: JWT token found for request {}", exchange.getRequest().getPath());

            if (jwtService.validateToken(token)) {
                String username = jwtService.extractUsername(token);

                Set<SimpleGrantedAuthority> authorities = jwtService.extractRoles(token).stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet());

                auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                mutatedExchange = exchange.mutate()
                        .request(exchange.getRequest().mutate()
                                .header("X-USERNAME", username)
                                .build())
                        .build();
            }
        }

       
        Mono<Void> filterChain = chain.filter(mutatedExchange);
        if (auth != null) {
            filterChain = filterChain.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
        }

        
        return filterChain.then(Mono.fromRunnable(() -> {
            log.info("Post-filter: Response status = {}", exchange.getResponse().getStatusCode());
        }));
    }
}
