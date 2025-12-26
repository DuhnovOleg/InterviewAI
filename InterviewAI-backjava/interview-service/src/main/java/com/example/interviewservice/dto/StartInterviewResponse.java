package com.example.interviewservice.dto;

import java.util.UUID;

public record StartInterviewResponse(UUID interviewId, String firstQuestion) { }