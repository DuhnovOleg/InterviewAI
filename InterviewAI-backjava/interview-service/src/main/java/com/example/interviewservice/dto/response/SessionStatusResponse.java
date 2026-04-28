package com.example.interviewservice.dto.response;

import lombok.Data;

@Data
public class SessionStatusResponse {

    private String sessionId;
    private String profession;
    private String level;
    private Boolean complete;
    private Boolean awaitingStopConfirmation;
    private Integer answersGiven;
    private Integer totalQuestions;
    private String progress;

}
