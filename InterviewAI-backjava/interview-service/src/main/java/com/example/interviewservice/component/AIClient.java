package com.example.interviewservice.component;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AIClient {

    public void classifyIntent(String prompt) {
        // TODO: вызов AI Service
    }

    public String generateQuestion(UUID interviewId, Integer step) {
        return "Расскажите о своём опыте работы.";
    }

    public void analyzeAnswer(String question, String answer) {
        // TODO: вызов AI Service
    }
}
