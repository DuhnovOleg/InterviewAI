package com.example.assessment_service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AttemptQuestionResponse {

    private UUID attemptId;
    private String question;
    private Integer questionNumber;
    private Integer totalQuestions;
    private Boolean voiceRequired;
    private Boolean cameraRequired;
    private Boolean recordingRequired;
    private Boolean interviewComplete;
    private String message;

}
