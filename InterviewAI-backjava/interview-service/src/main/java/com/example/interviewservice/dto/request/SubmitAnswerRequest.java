package com.example.interviewservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitAnswerRequest {

    @NotNull
    private String sessionId;

    @NotBlank
    private String answer;

    private String inputType = "text";
    private Double confidenceScore;
    private Double responseTimeSeconds;

}
