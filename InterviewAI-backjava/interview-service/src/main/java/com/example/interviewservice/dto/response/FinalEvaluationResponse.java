package com.example.interviewservice.dto.response;

import lombok.Data;

import java.util.Map;

@Data
public class FinalEvaluationResponse {

    private String sessionId;
    private Map<String, Object> report;

}
