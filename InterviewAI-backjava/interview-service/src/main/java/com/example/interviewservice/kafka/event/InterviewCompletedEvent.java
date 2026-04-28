package com.example.interviewservice.kafka.event;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class InterviewCompletedEvent {

    private String localSessionId;
    private String pythonSessionId;
    private Double averageScore;
    private String recommendation;
    private OffsetDateTime createdAt;

}
