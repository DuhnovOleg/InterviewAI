package com.example.reportservice.controller;

import com.example.reportservice.dto.CreateReportRequest;
import com.example.reportservice.dto.ReportResponse;
import com.example.reportservice.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportController {
    private final ReportService service;

    @PostMapping
    public ReportResponse create(@Valid @RequestBody CreateReportRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public ReportResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @GetMapping("/public/{token}")
    public ReportResponse getPublic(@PathVariable String token) {
        return service.getPublic(token);
    }

    @GetMapping("/owner/{ownerUserId}")
    public List<ReportResponse> byOwner(@PathVariable UUID ownerUserId) {
        return service.byOwner(ownerUserId);
    }

}
