package com.example.interviewservice.dto.python;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionFinalEvaluationRequest {

    @JsonProperty("session_id")
    private String sessionId;

}
