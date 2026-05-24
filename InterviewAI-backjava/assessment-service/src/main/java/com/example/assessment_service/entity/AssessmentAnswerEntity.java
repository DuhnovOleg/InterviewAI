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
@Table(name = "assessment_answers")
public class AssessmentAnswerEntity {

    @Id
    private UUID id;
    @Column(name = "attempt_id", nullable = false)
    private UUID attemptId;
    @Column(name = "question_id", nullable = false)
    private UUID questionId;
    @Column(name = "question_text", nullable = false, columnDefinition = "text")
    private String questionText;
    @Column(name = "answer_text", columnDefinition = "text")
    private String answerText;
    @Column(name = "input_type", nullable = false)
    private String inputType;
    @Column(name = "overall_score")
    private BigDecimal overallScore;
    @Column(name = "correctness_score")
    private BigDecimal correctnessScore;
    @Column(name = "completeness_score")
    private BigDecimal completenessScore;
    @Column(name = "clarity_score")
    private BigDecimal clarityScore;
    @Column(name = "relevance_score")
    private BigDecimal relevanceScore;
    @Column(name = "grammar_score")
    private BigDecimal grammarScore;
    @Column(columnDefinition = "text")
    private String feedback;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

}
