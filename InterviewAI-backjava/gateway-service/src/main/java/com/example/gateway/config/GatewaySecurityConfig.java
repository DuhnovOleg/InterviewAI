package com.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                .pathMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                                .pathMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                                .pathMatchers(HttpMethod.POST, "/api/v1/auth/refresh").permitAll()

                                .pathMatchers(HttpMethod.GET, "/api/v1/public/assessments/**").permitAll()
                                .pathMatchers(HttpMethod.POST, "/api/v1/public/assessments/**").permitAll()
                                .pathMatchers(HttpMethod.POST, "/api/v1/public/assessment-attempts/**").permitAll()
                                .pathMatchers(HttpMethod.GET, "/api/v1/public/assessment-results/**").permitAll()

                                .pathMatchers(HttpMethod.POST, "/api/v1/interviews/start").permitAll()
                                .pathMatchers(HttpMethod.POST, "/api/v1/interviews/answer").permitAll()
                                .pathMatchers(HttpMethod.POST, "/api/v1/interviews/answer/voice").permitAll()
                                .pathMatchers(HttpMethod.POST, "/api/v1/interviews/final-evaluation").permitAll()
                                .pathMatchers(HttpMethod.POST, "/api/v1/interviews/stop").permitAll()
                                .pathMatchers(HttpMethod.GET, "/api/v1/interviews/**").permitAll()

                                .pathMatchers("/actuator/health").permitAll()
                                .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

//                        .pathMatchers("/api/v1/assessments/**")
//                        .hasAnyAuthority("ROLE_HR", "ROLE_RECRUITER", "ROLE_ADMIN")
//
//                        .pathMatchers("/api/v1/assessment-attempts/**")
//                        .hasAnyAuthority("ROLE_HR", "ROLE_RECRUITER", "ROLE_ADMIN")

                                .pathMatchers("/api/v1/reports/**").authenticated()

                                .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
                }))
                .build();
    }

}
