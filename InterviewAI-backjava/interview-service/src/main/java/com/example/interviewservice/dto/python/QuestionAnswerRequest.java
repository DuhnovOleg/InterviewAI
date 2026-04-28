package com.example.interviewservice.dto.python;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QuestionAnswerRequest {

    @JsonProperty("session_id")
    private String sessionId;

    private String answer;

    @JsonProperty("input_type")
    private String inputType;

    @JsonProperty("confidence_score")
    private Double confidenceScore;

    @JsonProperty("response_time_seconds")
    private Double responseTimeSeconds;

}
