package com.example.interviewservice.kafka.event;

import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class InterviewStartedEvent {

    private String localSessionId;
    private String pythonSessionId;
    private UUID userId;
    private boolean authenticated;
    private String profession;
    private String level;
    private Integer totalQuestions;
    private OffsetDateTime createdAt;

}
