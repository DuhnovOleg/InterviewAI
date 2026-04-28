package com.example.authservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {

    private String issuer;
    private String secret;
    private long accessTokenExpirationMinutes;
    private long refreshTokenExpirationDays;

}
