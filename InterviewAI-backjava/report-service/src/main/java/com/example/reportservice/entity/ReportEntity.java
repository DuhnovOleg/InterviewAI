package com.example.reportservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "reports")
public class ReportEntity {

    @Id
    private UUID id;

    @Column(name = "source_type", nullable = false)
    private String sourceType;

    @Column(name = "source_id", nullable = false)
    private UUID sourceId;

    @Column(name = "owner_user_id")
    private UUID ownerUserId;

    @Column(name = "candidate_name")
    private String candidateName;

    @Column(name = "candidate_email")
    private String candidateEmail;

    @Column(name = "public_token", nullable = false, unique = true)
    private String publicToken;

    @Column(name = "overall_score")
    private BigDecimal overallScore;

    private String recommendation;

    @Column(columnDefinition = "text")
    private String summary;

    @Column(name = "report_json", nullable = false, columnDefinition = "text")
    private String reportJson;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

}
