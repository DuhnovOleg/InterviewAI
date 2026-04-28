package com.example.interviewservice.controller;

import com.example.interviewservice.dto.request.*;
import com.example.interviewservice.dto.response.FinalEvaluationResponse;
import com.example.interviewservice.dto.response.SessionStatusResponse;
import com.example.interviewservice.dto.response.StartInterviewResponse;
import com.example.interviewservice.dto.response.SubmitAnswerResponse;
import com.example.interviewservice.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/start")
    public StartInterviewResponse startInterview(@Valid @RequestBody StartInterviewRequest request) {
        return interviewService.startInterview(request);
    }

    @PostMapping("/answer")
    public SubmitAnswerResponse submitAnswer(@Valid @RequestBody SubmitAnswerRequest request) {
        return interviewService.submitAnswer(request);
    }

    @PostMapping("/answer/voice")
    public SubmitAnswerResponse submitVoiceAnswer(@Valid @RequestBody SubmitVoiceAnswerRequest request) {
        return interviewService.submitVoiceAnswer(request);
    }

    @PostMapping("/final-evaluation")
    public FinalEvaluationResponse finalEvaluation(@Valid @RequestBody FinalEvaluationRequest request) {
        return interviewService.finalEvaluation(request);
    }

    @PostMapping("/stop")
    public SubmitAnswerResponse stopInterview(@Valid @RequestBody StopInterviewRequest request) {
        return interviewService.stopInterview(request);
    }

    @GetMapping("/{sessionId}")
    public SessionStatusResponse getStatus(@PathVariable String sessionId) {
        return interviewService.getStatus(sessionId);
    }
}
