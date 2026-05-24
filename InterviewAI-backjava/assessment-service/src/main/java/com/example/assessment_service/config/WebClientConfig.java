package com.example.assessment_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient questionEngineWebClient(@Value("${clients.question-engine.base-url}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    public WebClient transcribeWebClient(@Value("${clients.transcribe.base-url}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    public WebClient reportWebClient(@Value("${clients.report.base-url}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

}
