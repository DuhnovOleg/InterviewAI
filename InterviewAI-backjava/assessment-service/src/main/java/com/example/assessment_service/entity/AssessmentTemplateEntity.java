package com.example.assessment_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "assessment_templates")
public class AssessmentTemplateEntity {

    @Id
    private UUID id;
    @Column(name = "owner_hr_user_id", nullable = false)
    private UUID ownerHrUserId;
    @Column(nullable = false)
    private String title;
    @Column(columnDefinition = "text")
    private String description;
    private String profession;
    private String level;
    @Column(name = "public_token", nullable = false, unique = true)
    private String publicToken;
    @Column(name = "voice_required", nullable = false)
    private Boolean voiceRequired;
    @Column(name = "camera_required", nullable = false)
    private Boolean cameraRequired;
    @Column(name = "recording_required", nullable = false)
    private Boolean recordingRequired;
    @Column(name = "text_answers_allowed", nullable = false)
    private Boolean textAnswersAllowed;
    @Column(nullable = false)
    private Boolean active;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

}
