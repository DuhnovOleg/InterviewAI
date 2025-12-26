package com.example.interviewservice.mapper;

import com.example.interviewservice.dto.StartInterviewRequest;
import com.example.interviewservice.entity.Interview;
import com.example.interviewservice.enums.InterviewStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class InterviewMapper {

    public Interview fromStartRequest(StartInterviewRequest request) {
        return Interview.builder()
                .status(InterviewStatus.CREATED)
                .rawUserPrompt(request.initialPrompt())
                .currentStep(0)
                .maxSteps(10)
                .startedAt(Instant.now())
                .build();
    }

}
