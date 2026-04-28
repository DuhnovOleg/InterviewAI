package com.example.interviewservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FinalEvaluationRequest {

    @NotBlank
    private String sessionId;

}
