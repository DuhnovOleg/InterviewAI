package com.example.assessment_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubmitAssessmentAnswerRequest {

    @NotBlank
    private String answer;
    private String inputType = "text";

}
