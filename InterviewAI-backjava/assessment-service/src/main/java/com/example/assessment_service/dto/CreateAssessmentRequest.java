package com.example.assessment_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateAssessmentRequest {

    @NotBlank
    private String title;
    private String description;
    private String profession;
    private String level;
    private Boolean voiceRequired = false;
    private Boolean cameraRequired = false;
    private Boolean recordingRequired = false;
    private Boolean textAnswersAllowed = true;
    @Valid
    @NotEmpty
    private List<QuestionDto> questions;

    @Data
    public static class QuestionDto {
        @NotBlank
        private String text;
        private String expectedAnswer;
        private String skillTag;
    }

}
