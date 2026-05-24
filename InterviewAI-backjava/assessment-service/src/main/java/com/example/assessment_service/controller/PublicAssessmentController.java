package com.example.assessment_service.controller;

import com.example.assessment_service.dto.*;
import com.example.assessment_service.service.AssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public")
public class PublicAssessmentController {

    private final AssessmentService service;

    @GetMapping("/assessments/{token}")
    public PublicAssessmentResponse get(@PathVariable String token) {
        return service.publicAssessment(token);
    }

    @PostMapping("/assessments/{token}/start")
    public AttemptQuestionResponse start(@PathVariable String token, @Valid @RequestBody StartAttemptRequest req) {
        return service.start(token, req);
    }

    @PostMapping("/assessment-attempts/{attemptId}/answer")
    public SubmitAssessmentAnswerResponse answer(@PathVariable UUID attemptId, @Valid @RequestBody SubmitAssessmentAnswerRequest req) {
        return service.answer(attemptId, req);
    }

    @PostMapping("/assessment-attempts/{attemptId}/answer/voice")
    public SubmitAssessmentAnswerResponse voice(@PathVariable UUID attemptId, @Valid @RequestBody SubmitAssessmentVoiceRequest req) {
        return service.voice(attemptId, req);
    }

    @GetMapping("/assessment-results/{token}")
    public AssessmentAttemptDetailsResponse result(@PathVariable String token) {
        return service.publicResult(token);
    }

}
