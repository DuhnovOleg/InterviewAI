package com.example.interviewservice.dto.python;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QuestionStartResponse {

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("profession")
    private String profession;

    @JsonProperty("level")
    private String level;

    @JsonProperty("total_questions")
    private Integer totalQuestions;

    @JsonProperty("question")
    private String question;

    @JsonProperty("question_number")
    private Integer questionNumber;

    @JsonProperty("message")
    private String message;

}
