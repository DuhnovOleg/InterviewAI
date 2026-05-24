package com.example.assessment_service.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;
import java.time.OffsetDateTime;

@Data
public class AssessmentResponse {

    private UUID id;
    private UUID ownerHrUserId;
    private String title;
    private String description;
    private String profession;
    private String level;
    private String publicToken;
    private String publicUrl;
    private Boolean voiceRequired;
    private Boolean cameraRequired;
    private Boolean recordingRequired;
    private Boolean textAnswersAllowed;
    private List<Question> questions;
    private Integer attemptsCount;
    private OffsetDateTime createdAt;

    @Data
    public static class Question {
        private UUID id;
        private String text;
        private Integer position;
    }

}
