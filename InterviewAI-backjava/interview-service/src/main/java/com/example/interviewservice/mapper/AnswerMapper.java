//package com.example.interviewservice.mapper;
//
//import com.example.interviewservice.entity.InterviewAnswerEntity;
//import com.example.interviewservice.entity.InterviewReportEntity;
//import org.springframework.stereotype.Component;
//
//import java.time.Instant;
//
//@Component
//public class AnswerMapper {
//
//    public InterviewAnswerEntity toAnswer(InterviewReportEntity question, AnswerRequest request) {
//        return InterviewAnswerEntity.builder()
//                .question(question)
//                .answerText(request.answer())
//                .answerSource(request.source())
//                .answeredAt(Instant.now())
//                .build();
//    }
//
//}
