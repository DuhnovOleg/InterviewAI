package com.example.interviewservice.kafka.event;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class InterviewAnswerSubmittedEvent {

    private String localSessionId;
    private String pythonSessionId;
    private String inputType;
    private OffsetDateTime createdAt;

}
