package com.example.interviewservice.mapper;

import com.example.interviewservice.entity.Interview;
import com.example.interviewservice.entity.Question;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class QuestionMapper {

    public Question toQuestion(Interview interview, String questionText, int step) {
        return Question.builder()
                .interview(interview)
                .stepNumber(step)
                .questionText(questionText)
                .askedAt(Instant.now())
                .build();
    }

}
