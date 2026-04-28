package com.example.interviewservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "interview_reports")
@Getter
@Setter
public class InterviewReportEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "interview_session_id", nullable = false, unique = true)
    private UUID interviewSessionId;

    @Column(name = "overall_score", precision = 4, scale = 2)
    private BigDecimal overallScore;

    @Column(name = "technical_score", precision = 4, scale = 2)
    private BigDecimal technicalScore;

    @Column(name = "correctness_score", precision = 4, scale = 2)
    private BigDecimal correctnessScore;

    @Column(name = "completeness_score", precision = 4, scale = 2)
    private BigDecimal completenessScore;

    @Column(name = "clarity_score", precision = 4, scale = 2)
    private BigDecimal clarityScore;

    @Column(name = "relevance_score", precision = 4, scale = 2)
    private BigDecimal relevanceScore;

    @Column(name = "grammar_score", precision = 4, scale = 2)
    private BigDecimal grammarScore;

    @Column(name = "confidence_score", precision = 4, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "response_speed_score", precision = 4, scale = 2)
    private BigDecimal responseSpeedScore;

    @Column(name = "hire_recommendation")
    private String hireRecommendation;

    @Column(name = "recommended_level")
    private String recommendedLevel;

    @Column(columnDefinition = "text")
    private String summary;

    @Column(name = "report_json", columnDefinition = "text")
    private String reportJson;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

}
