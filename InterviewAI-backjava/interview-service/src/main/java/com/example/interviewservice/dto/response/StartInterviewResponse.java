package com.example.interviewservice.dto.response;

import lombok.Data;

@Data
public class StartInterviewResponse {

    private String sessionId;
    private String profession;
    private String level;
    private Integer totalQuestions;
    private String question;
    private Integer questionNumber;
    private String message;
    private Boolean registeredUser;

}
