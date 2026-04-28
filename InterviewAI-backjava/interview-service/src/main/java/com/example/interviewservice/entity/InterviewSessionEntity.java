package com.example.interviewservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "interview_sessions")
@Getter
@Setter
public class InterviewSessionEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "python_session_id", nullable = false, unique = true)
    private String pythonSessionId;

    private String profession;
    private String level;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    private String status;

    @Column(name = "awaiting_stop_confirmation")
    private Boolean awaitingStopConfirmation = false;

    @Column(name = "started_at", nullable = false)
    private OffsetDateTime startedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

}
