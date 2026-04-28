package com.example.interviewservice.kafka.event;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class VoiceAnswerSubmittedEvent {

    private String localSessionId;
    private String pythonSessionId;
    private OffsetDateTime createdAt;

}
