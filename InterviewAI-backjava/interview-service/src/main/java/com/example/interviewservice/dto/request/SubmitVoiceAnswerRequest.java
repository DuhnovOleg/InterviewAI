package com.example.interviewservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitVoiceAnswerRequest {

    @NotBlank
    private String sessionId;

    @NotBlank
    private String audioBase64;

    private Double responseTimeSeconds;

}
