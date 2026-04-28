package com.example.interviewservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StartInterviewRequest {

    @NotBlank
    private String message;

}
