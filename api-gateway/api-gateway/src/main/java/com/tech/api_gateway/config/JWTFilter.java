package com.tech.api_gateway.config;

import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    private final RedisTemplate<String, Object> redisTemplate;
    private final Logger log = LoggerFactory.getLogger(JWTFilter.class);
    private final JWTService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        if (exchange.getAttributes().containsKey("jwtFilterExecuted")) {
            return chain.filter(exchange);
        }
        exchange.getAttributes().put("jwtFilterExecuted", true);

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

 
        if (!jwtService.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        
        Object userId = redisTemplate.opsForValue().get(token);
        if (userId == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        log.info("REDIS : {}" ,userId);

       
        String username = jwtService.extractUsername(token);
        Set<SimpleGrantedAuthority> authorities = jwtService.extractRoles(token).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-USERNAME", username)
                        .header("X-USER-ID", String.valueOf(userId))
                        .build())
                .build();

        log.info("JWT validated and session active for user {}", username);

        return chain.filter(mutatedExchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
                    .then(Mono.fromRunnable(() ->
                        log.info("Post-filter: Response status = {}", exchange.getResponse().getStatusCode())));
    }
}

