package com.example.interviewservice.mapper;

import com.example.interviewservice.dto.AnswerRequest;
import com.example.interviewservice.entity.Answer;
import com.example.interviewservice.entity.Question;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class AnswerMapper {

    public Answer toAnswer(Question question, AnswerRequest request) {
        return Answer.builder()
                .question(question)
                .answerText(request.answer())
                .answerSource(request.source())
                .answeredAt(Instant.now())
                .build();
    }

}
