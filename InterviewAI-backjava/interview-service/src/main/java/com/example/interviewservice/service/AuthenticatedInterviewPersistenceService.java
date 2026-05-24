package com.example.interviewservice.service;

import com.example.interviewservice.entity.InterviewAnswerEntity;
import com.example.interviewservice.entity.InterviewReportEntity;
import com.example.interviewservice.entity.InterviewSessionEntity;
import com.example.interviewservice.repository.InterviewAnswerRepository;
import com.example.interviewservice.repository.InterviewReportRepository;
import com.example.interviewservice.repository.InterviewSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticatedInterviewPersistenceService {

    private final InterviewSessionRepository sessionRepository;
    private final InterviewAnswerRepository answerRepository;
    private final InterviewReportRepository reportRepository;
    private final ObjectMapper objectMapper;

    public InterviewSessionEntity createSession(
            UUID userId,
            String pythonSessionId,
            String profession,
            String level,
            Integer totalQuestions
    ) {
        InterviewSessionEntity entity = new InterviewSessionEntity();
        entity.setUserId(userId);
        entity.setPythonSessionId(pythonSessionId);
        entity.setProfession(profession);
        entity.setLevel(level);
        entity.setTotalQuestions(totalQuestions);
        entity.setStatus("IN_PROGRESS");
        entity.setAwaitingStopConfirmation(false);
        entity.setStartedAt(OffsetDateTime.now());
        entity.setCreatedAt(OffsetDateTime.now());
        entity.setUpdatedAt(OffsetDateTime.now());
        return sessionRepository.save(entity);
    }

    public InterviewSessionEntity findByPythonSessionId(String pythonSessionId) {
        return sessionRepository.findByPythonSessionId(pythonSessionId).orElse(null);
    }

    public void saveAnswer(
            UUID interviewSessionId,
            Integer questionNumber,
            String questionText,
            String answerText,
            String inputType,
            Double overallScore,
            String feedback
    ) {
        InterviewAnswerEntity entity = new InterviewAnswerEntity();
        entity.setInterviewSessionId(interviewSessionId);
        entity.setQuestionNumber(questionNumber);
        entity.setQuestionText(questionText);
        entity.setAnswerText(answerText);
        entity.setInputType(inputType);
        entity.setOverallScore(toBigDecimal(overallScore));
        entity.setFeedback(feedback);
        entity.setAnsweredAt(OffsetDateTime.now());
        answerRepository.save(entity);
    }

    public java.util.List<java.util.Map<String, Object>> recentHistory(UUID userId) {
        return sessionRepository.findTop10ByUserIdOrderByStartedAtDesc(userId).stream().map(session -> {
            java.util.Map<String, Object> item = new java.util.LinkedHashMap<>();
            item.put("sessionId", session.getId().toString());
            item.put("profession", session.getProfession());
            item.put("level", session.getLevel());
            item.put("createdAt", session.getStartedAt() == null ? null : session.getStartedAt().toString());
            var report = reportRepository.findByInterviewSessionId(session.getId()).orElse(null);
            item.put("averageScore", report == null ? null : report.getOverallScore());
            item.put("recommendation", report == null ? "" : report.getHireRecommendation());
            return item;
        }).toList();
    }

    private BigDecimal toBigDecimal(Double value) {
        return value == null ? null : BigDecimal.valueOf(value);
    }

    public void markCompleted(UUID sessionId) {
        InterviewSessionEntity entity = sessionRepository.findById(sessionId).orElseThrow();
        entity.setStatus("COMPLETED");
        entity.setCompletedAt(OffsetDateTime.now());
        entity.setUpdatedAt(OffsetDateTime.now());
        sessionRepository.save(entity);
    }

    public InterviewSessionEntity findByLocalSessionId(UUID localSessionId) {
        return sessionRepository.findById(localSessionId).orElse(null);
    }

    @SuppressWarnings("unchecked")
    public void saveReport(UUID interviewSessionId, Object reportObject) {
        Map<String, Object> report = (Map<String, Object>) reportObject;

        InterviewReportEntity entity = reportRepository.findByInterviewSessionId(interviewSessionId)
                .orElseGet(InterviewReportEntity::new);
        entity.setInterviewSessionId(interviewSessionId);
        entity.setOverallScore(readBigDecimal(report.get("overall_score")));
        entity.setTechnicalScore(readBigDecimal(report.get("technical_score")));
        entity.setCorrectnessScore(readBigDecimal(report.get("correctness_score")));
        entity.setCompletenessScore(readBigDecimal(report.get("completeness_score")));
        entity.setClarityScore(readBigDecimal(report.get("clarity_score")));
        entity.setRelevanceScore(readBigDecimal(report.get("relevance_score")));
        entity.setGrammarScore(readBigDecimal(report.get("grammar_score")));
        entity.setConfidenceScore(readBigDecimal(report.get("confidence_score")));
        entity.setResponseSpeedScore(readBigDecimal(report.get("response_speed_score")));
        entity.setHireRecommendation(readString(report.get("hire_recommendation")));
        entity.setRecommendedLevel(readString(report.get("recommended_level")));
        entity.setSummary(readString(report.get("summary")));
        try {
            entity.setReportJson(objectMapper.writeValueAsString(report));
        } catch (Exception e) {
            entity.setReportJson("{}");
        }
        entity.setCreatedAt(OffsetDateTime.now());

        reportRepository.save(entity);
    }

    private BigDecimal readBigDecimal(Object value) {
        if (value == null) return null;
        if (value instanceof BigDecimal bigDecimal) return bigDecimal;
        if (value instanceof Number number) return BigDecimal.valueOf(number.doubleValue());
        return new BigDecimal(value.toString());
    }

    private String readString(Object value) {
        return value == null ? null : value.toString();
    }

}
