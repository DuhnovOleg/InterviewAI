package com.example.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

@Configuration
public class KeyResolverConfig {

    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest()
                        .getRemoteAddress() != null
                        ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                        : "unknown-ip"
        );
    }

    @Bean
    @Primary
    public KeyResolver userOrIpKeyResolver() {
        return exchange -> exchange
                .getPrincipal()
                .cast(JwtAuthenticationToken.class)
                .map(jwt -> {
                    Object sub = jwt.getToken().getClaims().get("sub");
                    return sub != null ? "user:" + sub : "anonymous";
                })
                .switchIfEmpty(Mono.fromSupplier(() ->
                        exchange.getRequest().getRemoteAddress() != null
                                ? "ip:" + exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                                : "ip:unknown"
                ));
    }

}
