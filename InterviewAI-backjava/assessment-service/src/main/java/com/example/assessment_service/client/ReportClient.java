package com.example.assessment_service.client;

import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
public class ReportClient {

    private final WebClient webClient;

    public ReportClient(@Qualifier("reportWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Response create(CreateRequest request) {
        return webClient.post().uri("/api/v1/reports").contentType(MediaType.APPLICATION_JSON).bodyValue(request).retrieve().bodyToMono(Response.class).block();
    }

    @Data
    public static class CreateRequest {
        private String sourceType;
        private UUID sourceId;
        private UUID ownerUserId;
        private String candidateName;
        private String candidateEmail;
        private BigDecimal overallScore;
        private String recommendation;
        private String summary;
        private Map<String, Object> reportPayload;
    }

    @Data
    public static class Response {
        private UUID id;
        private String publicToken;
        private String summary;
    }

}
