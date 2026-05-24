package com.example.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class UserHeadersGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .cast(Authentication.class)
                .flatMap(authentication -> {
                    Object principal = authentication.getPrincipal();

                    if (!(principal instanceof Jwt jwt)) {
                        return chain.filter(exchange);
                    }

                    ServerHttpRequest request = exchange.getRequest()
                            .mutate()
                            .headers(headers -> {
                                headers.remove("X-User-Id");
                                headers.remove("X-User-Email");
                                headers.remove("X-Username");

                                headers.add("X-User-Id", jwt.getSubject());

                                String email = jwt.getClaimAsString("email");
                                String username = jwt.getClaimAsString("username");

                                if (email != null) {
                                    headers.add("X-User-Email", email);
                                }

                                if (username != null) {
                                    headers.add("X-Username", username);
                                }
                            })
                            .build();

                    return chain.filter(exchange.mutate().request(request).build());
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}