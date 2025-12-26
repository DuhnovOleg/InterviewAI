package com.example.interviewservice.controller;

import com.example.interviewservice.dto.AnswerRequest;
import com.example.interviewservice.dto.StartInterviewRequest;
import com.example.interviewservice.dto.StartInterviewResponse;
import com.example.interviewservice.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/start")
    public StartInterviewResponse startInterview(@RequestBody StartInterviewRequest request) {
        return interviewService.startInterview(request);
    }

    @PostMapping("/{id}/answers}")
    public String submitAnswer(@PathVariable String id, @RequestBody AnswerRequest request) {
        interviewService.submitAnswer(id, request);
        return null;
    }



}
