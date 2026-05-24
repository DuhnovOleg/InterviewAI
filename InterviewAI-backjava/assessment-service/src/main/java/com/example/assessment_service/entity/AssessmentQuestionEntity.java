package com.example.assessment_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "assessment_questions")
public class AssessmentQuestionEntity {

    @Id
    private UUID id;
    @Column(name = "template_id", nullable = false)
    private UUID templateId;
    @Column(name = "question_text", nullable = false, columnDefinition = "text")
    private String questionText;
    @Column(nullable = false)
    private Integer position;
    @Column(name = "expected_answer", columnDefinition = "text")
    private String expectedAnswer;
    @Column(name = "skill_tag")
    private String skillTag;
    @Column(nullable = false)
    private Boolean required;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

}
