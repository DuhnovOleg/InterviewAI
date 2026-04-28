//package com.example.interviewservice.mapper;
//
//import com.example.interviewservice.entity.InterviewSessionEntity;
//import com.example.interviewservice.entity.InterviewReportEntity;
//import org.springframework.stereotype.Component;
//
//import java.time.Instant;
//
//@Component
//public class QuestionMapper {
//
//    public InterviewReportEntity toQuestion(InterviewSessionEntity interview, String questionText, int step) {
//        return InterviewReportEntity.builder()
//                .interview(interview)
//                .stepNumber(step)
//                .questionText(questionText)
//                .askedAt(Instant.now())
//                .build();
//    }
//
//}
