package com.example.assessment_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StartAttemptRequest {

    @NotBlank
    private String candidateName;
    private String candidateEmail;

}
