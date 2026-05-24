package com.example.assessment_service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class SubmitAssessmentAnswerResponse {

    private UUID attemptId;
    private String transcribedText;
    private String feedback;
    private Double previousScore;
    private String question;
    private Integer questionNumber;
    private Integer totalQuestions;
    private Boolean interviewComplete;
    private UUID reportId;
    private String resultPublicUrl;
    private String resultPublicToken;
    private String message;

}
