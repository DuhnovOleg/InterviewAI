package com.example.interviewservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "interview_answers")
@Getter
@Setter
public class InterviewAnswerEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "interview_session_id", nullable = false)
    private UUID interviewSessionId;

    @Column(name = "question_number")
    private Integer questionNumber;

    @Column(name = "question_text", columnDefinition = "text")
    private String questionText;

    @Column(name = "answer_text", columnDefinition = "text")
    private String answerText;

    @Column(name = "overall_score", precision = 4, scale = 2)
    private BigDecimal overallScore;

    @Column(columnDefinition = "text")
    private String feedback;

    @Column(name = "input_type")
    private String inputType;

    @Column(name = "answered_at", nullable = false)
    private OffsetDateTime answeredAt;

}
