package com.example.reportservice.service;

import com.example.reportservice.dto.CreateReportRequest;
import com.example.reportservice.dto.ReportResponse;
import com.example.reportservice.entity.ReportEntity;
import com.example.reportservice.repository.ReportRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository repository;
    private final ObjectMapper objectMapper;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public ReportResponse create(CreateReportRequest request) {
        ReportEntity entity = new ReportEntity();
        entity.setId(UUID.randomUUID());
        entity.setSourceType(request.getSourceType());
        entity.setSourceId(request.getSourceId());
        entity.setOwnerUserId(request.getOwnerUserId());
        entity.setCandidateName(request.getCandidateName());
        entity.setCandidateEmail(request.getCandidateEmail());
        entity.setOverallScore(request.getOverallScore());
        entity.setRecommendation(request.getRecommendation());
        entity.setSummary(request.getSummary());
        entity.setPublicToken(generateToken());
        entity.setCreatedAt(OffsetDateTime.now());
        try {
            entity.setReportJson(objectMapper.writeValueAsString(request.getReportPayload()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid report payload");
        }
        return toResponse(repository.save(entity));
    }

    public ReportResponse get(UUID id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found")));
    }

    public ReportResponse getPublic(String token) {
        return toResponse(repository.findByPublicToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found")));
    }

    public List<ReportResponse> byOwner(UUID ownerUserId) {
        return repository.findByOwnerUserIdOrderByCreatedAtDesc(ownerUserId).stream().map(this::toResponse).toList();
    }

    private ReportResponse toResponse(ReportEntity e) {
        ReportResponse r = new ReportResponse();
        r.setId(e.getId());
        r.setSourceType(e.getSourceType());
        r.setSourceId(e.getSourceId());
        r.setOwnerUserId(e.getOwnerUserId());
        r.setCandidateName(e.getCandidateName());
        r.setCandidateEmail(e.getCandidateEmail());
        r.setPublicToken(e.getPublicToken());
        r.setOverallScore(e.getOverallScore());
        r.setRecommendation(e.getRecommendation());
        r.setSummary(e.getSummary());
        r.setCreatedAt(e.getCreatedAt());
        try {
            r.setReportPayload(objectMapper.readValue(e.getReportJson(), new TypeReference<Map<String, Object>>() {}));
        } catch (Exception ex) {
            r.setReportPayload(Map.of());
        }
        return r;
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
