package com.example.assessment_service.controller;

import com.example.assessment_service.dto.*;
import com.example.assessment_service.service.AssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assessments")
public class HrAssessmentController {

    private final AssessmentService service;

    @PostMapping
    public AssessmentResponse create(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreateAssessmentRequest request
    ) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "X-User-Id header is missing");
        }

        UUID ownerHrUserId = UUID.fromString(userId);
        return service.create(ownerHrUserId, request);
    }

    @GetMapping("/my")
    public List<AssessmentResponse> my(
            @RequestHeader(value = "X-User-Id", required = false) String userId
    ) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "X-User-Id header is missing");
        }

        UUID ownerHrUserId = UUID.fromString(userId);
        return service.my(ownerHrUserId);
    }

    @GetMapping("/{assessmentId}/attempts")
    public List<AssessmentAttemptListItemResponse> attempts(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable UUID assessmentId
    ) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "X-User-Id header is missing");
        }

        UUID ownerHrUserId = UUID.fromString(userId);
        return service.getAttempts(ownerHrUserId, assessmentId);
    }

    @GetMapping("/attempts/{attemptId}")
    public AssessmentAttemptDetailsResponse attemptDetails(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable UUID attemptId
    ) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "X-User-Id header is missing");
        }

        UUID ownerHrUserId = UUID.fromString(userId);
        return service.getAttemptDetails(ownerHrUserId, attemptId);
    }

}
