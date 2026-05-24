package com.example.assessment_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubmitAssessmentVoiceRequest {

    @NotBlank
    private String audioBase64;

}
