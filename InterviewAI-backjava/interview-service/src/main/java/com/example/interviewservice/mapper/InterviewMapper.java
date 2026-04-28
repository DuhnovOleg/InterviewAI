//package com.example.interviewservice.mapper;
//
//import com.example.interviewservice.dto.request.StartInterviewRequest;
//import com.example.interviewservice.entity.InterviewSessionEntity;
//import com.example.interviewservice.enums.InterviewStatus;
//import org.springframework.stereotype.Component;
//
//import java.time.Instant;
//
//@Component
//public class InterviewMapper {
//
//    public InterviewSessionEntity fromStartRequest(StartInterviewRequest request) {
//        return InterviewSessionEntity.builder()
//                .status(InterviewStatus.CREATED)
//                .rawUserPrompt(request.initialPrompt())
//                .currentStep(0)
//                .maxSteps(10)
//                .startedAt(Instant.now())
//                .build();
//    }
//
//}
