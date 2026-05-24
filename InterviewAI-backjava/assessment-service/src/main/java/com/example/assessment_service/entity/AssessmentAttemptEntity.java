package com.example.assessment_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "assessment_attempts")
public class AssessmentAttemptEntity {

    @Id
    private UUID id;
    @Column(name = "template_id", nullable = false)
    private UUID templateId;
    @Column(name = "candidate_name", nullable = false)
    private String candidateName;
    @Column(name = "candidate_email")
    private String candidateEmail;
    @Column(nullable = false)
    private String status;
    @Column(name = "current_position", nullable = false)
    private Integer currentPosition;
    @Column(name = "result_token")
    private String resultToken;
    @Column(name = "report_id")
    private UUID reportId;
    @Column(name = "overall_score")
    private BigDecimal overallScore;
    private String recommendation;
    @Column(name = "started_at", nullable = false)
    private OffsetDateTime startedAt;
    @Column(name = "completed_at")
    private OffsetDateTime completedAt;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

}
