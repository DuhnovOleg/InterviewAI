package com.example.assessment_service.dto;

import lombok.Data;

@Data
public class PublicAssessmentResponse {

    private String title;
    private String description;
    private String profession;
    private String level;
    private Boolean voiceRequired;
    private Boolean cameraRequired;
    private Boolean recordingRequired;
    private Boolean textAnswersAllowed;
    private Integer questionsCount;

}
