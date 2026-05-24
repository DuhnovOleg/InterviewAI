package com.example.assessment_service.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TranscribeClient {

    private final WebClient webClient;

    public TranscribeClient(@Qualifier("transcribeWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Response transcribe(String audioBase64) {
        Request r = new Request();
        r.setAudioBase64(audioBase64);
        r.setLanguageHint("ru");
        return webClient.post().uri("/api/v1/transcribe").contentType(MediaType.APPLICATION_JSON).bodyValue(r).retrieve().bodyToMono(Response.class).block();
    }

    @Data
    public static class Request {

        private String audioBase64;
        private String languageHint;

    }

    @Data
    public static class Response {

        private String text;
        private String language;

        @JsonProperty("duration_seconds")
        private Double durationSeconds;

        private String model;

    }

}
