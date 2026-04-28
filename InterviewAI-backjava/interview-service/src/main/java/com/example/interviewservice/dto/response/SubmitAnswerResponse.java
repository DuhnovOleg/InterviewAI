package com.example.interviewservice.dto.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SubmitAnswerResponse {

    private String sessionId;
    private String question;
    private Integer questionNumber;
    private Integer totalQuestions;
    private Boolean interviewComplete;
    private Boolean awaitingStopConfirmation;
    private Double previousScore;
    private String feedback;
    private List<String> strengths;
    private List<String> weaknesses;
    private String message;
    private Map<String, Object> finalPayload;
    private String transcribedText;

}
