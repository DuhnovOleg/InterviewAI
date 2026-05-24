package com.example.reportservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
public class CreateReportRequest {

    @NotBlank
    private String sourceType;
    @NotNull
    private UUID sourceId;
    private UUID ownerUserId;
    private String candidateName;
    private String candidateEmail;
    private BigDecimal overallScore;
    private String recommendation;
    private String summary;
    @NotNull
    private Map<String, Object> reportPayload;

}
