package com.example.gateway.filter;

import com.example.gateway.util.SecurityHeaderNames;
import org.springframework.cloud.gateway.filter.*;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class UserHeaderPropagationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .cast(JwtAuthenticationToken.class)
                .flatMap(jwtAuthenticationToken -> {
                    String userId = jwtAuthenticationToken.getToken().getSubject();

                    Collection<String> authorities = jwtAuthenticationToken.getAuthorities()
                            .stream()
                            .map(a -> a.getAuthority())
                            .collect(Collectors.toList());

                    ServerHttpRequest mutatedRequest = exchange.getRequest()
                            .mutate()
                            .header(SecurityHeaderNames.X_USER_ID, userId != null ? userId : "")
                            .header(SecurityHeaderNames.X_USER_ROLE, String.join(",", authorities))
                            .build();

                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return -50;
    }

}
