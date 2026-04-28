package com.example.interviewservice.dto.python;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class QuestionAnswerResponse {

    @JsonProperty("session_id")
    private String sessionId;

    private String question;

    @JsonProperty("question_number")
    private Integer questionNumber;

    @JsonProperty("total_questions")
    private Integer totalQuestions;

    @JsonProperty("interview_complete")
    private Boolean interviewComplete;

    @JsonProperty("awaiting_stop_confirmation")
    private Boolean awaitingStopConfirmation;

    @JsonProperty("previous_score")
    private Double previousScore;

    private String feedback;
    private List<String> strengths;
    private List<String> weaknesses;
    private String message;

    private String verdict;
    private String recommendation;
    private Double averageScore;

    private Map<String, Object> raw;

}
