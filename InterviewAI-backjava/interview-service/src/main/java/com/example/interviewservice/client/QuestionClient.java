package com.example.interviewservice.client;

import com.example.interviewservice.dto.python.*;
import com.example.interviewservice.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class QuestionClient {

    private final WebClient webClient;

    public QuestionClient(@Qualifier("questionWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public QuestionStartResponse startInterview(QuestionStartRequest request) {
        try {
            return webClient.post()
                    .uri("/api/v1/interviews/start")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(QuestionStartResponse.class)
                    .block();
        } catch (Exception ex) {
            throw new ExternalServiceException("Question engine start interview failed", ex);
        }
    }

    public QuestionAnswerResponse submitAnswer(QuestionAnswerRequest request) {
        try {
            return webClient.post()
                    .uri("/api/v1/interviews/answer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(QuestionAnswerResponse.class)
                    .block();
        } catch (Exception ex) {
            throw new ExternalServiceException("Question engine submit answer failed", ex);
        }
    }

    public Object finalEvaluation(QuestionFinalEvaluationRequest request) {
        try {
            return webClient.post()
                    .uri("/api/v1/interviews/final_evaluation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (Exception ex) {
            throw new ExternalServiceException("Question engine final evaluation failed", ex);
        }
    }
}
