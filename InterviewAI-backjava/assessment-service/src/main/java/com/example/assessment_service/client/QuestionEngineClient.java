package com.example.assessment_service.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
public class QuestionEngineClient {

    private final WebClient webClient;

    public QuestionEngineClient(@Qualifier("questionEngineWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public AnswerEvaluation evaluateAnswer(String profession, String level, String question, String answer) {
        Request r = new Request();
        r.setProfession(profession);
        r.setLevel(level);
        r.setQuestion(question);
        r.setAnswer(answer);
        return webClient
                .post()
                .uri("/api/v1/interviews/evaluate_answer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(r)
                .retrieve()
                .bodyToMono(AnswerEvaluation.class)
                .block();
    }

    public Map<String, Object> finalReport(Map<String, Object> payload) {
        return webClient
                .post()
                .uri("/api/v1/evaluate-interview-final")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    @Data
    public static class Request {
        private String profession;
        private String level;
        private String question;
        private String answer;
    }

    @Data
    public static class AnswerEvaluation {
        @JsonProperty("overall_score")
        private Double overallScore;
        @JsonProperty("correctness_score")
        private Double correctnessScore;
        @JsonProperty("completeness_score")
        private Double completenessScore;
        @JsonProperty("clarity_score")
        private Double clarityScore;
        @JsonProperty("relevance_score")
        private Double relevanceScore;
        @JsonProperty("grammar_score")
        private Double grammarScore;
        private String feedback;
        private List<String> strengths;
        private List<String> weaknesses;
    }

}
