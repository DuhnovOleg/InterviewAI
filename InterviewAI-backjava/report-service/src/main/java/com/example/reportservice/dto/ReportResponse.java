package com.example.reportservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class ReportResponse {

    private UUID id;
    private String sourceType;
    private UUID sourceId;
    private UUID ownerUserId;
    private String candidateName;
    private String candidateEmail;
    private String publicToken;
    private BigDecimal overallScore;
    private String recommendation;
    private String summary;
    private Map<String, Object> reportPayload;
    private OffsetDateTime createdAt;

}
