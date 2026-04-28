package com.example.interviewservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GuestInterviewState {

    private String pythonSessionId;
    private String profession;
    private String level;
    private Integer totalQuestions;
    private Boolean awaitingStopConfirmation;

}
