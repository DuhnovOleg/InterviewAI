package com.example.interviewservice.entity;

import com.example.interviewservice.enums.InterviewStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "interviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interview {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    private InterviewStatus status;

    private String rawUserPrompt;

    private int currentStep;
    private int maxSteps;

    private Instant startedAt;
    private Instant finishedAt;

    public void start() {
        this.status = InterviewStatus.IN_PROGRESS;
        this.startedAt = Instant.now();
    }

    public void nextStep() {
        this.currentStep++;
    }

    public void finish() {
        this.status = InterviewStatus.FINISHED;
        this.finishedAt = Instant.now();
    }

    public void cancel() {
        this.status = InterviewStatus.CANCELLED;
        this.finishedAt = Instant.now();
    }

}
