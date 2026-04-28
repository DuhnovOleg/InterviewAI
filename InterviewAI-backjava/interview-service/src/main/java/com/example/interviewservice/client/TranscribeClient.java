package com.example.interviewservice.client;

import com.example.interviewservice.dto.python.TranscribeRequest;
import com.example.interviewservice.dto.python.TranscribeResponse;
import com.example.interviewservice.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class TranscribeClient {

    private final WebClient webClient;

    public TranscribeClient(@Qualifier("transcribeWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public TranscribeResponse transcribe(TranscribeRequest request) {
        try {
            return webClient.post()
                    .uri("/api/v1/transcribe")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(TranscribeResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            throw new ExternalServiceException(
                    "Transcribe service failed: status=" + ex.getStatusCode() +
                            ", body=" + ex.getResponseBodyAsString(),
                    ex
            );
        } catch (Exception ex) {
            throw new ExternalServiceException("Transcribe service failed: " + ex.getMessage(), ex);
        }
    }

}
