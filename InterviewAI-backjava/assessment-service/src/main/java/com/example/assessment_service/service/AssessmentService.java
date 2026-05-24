package com.example.assessment_service.service;

import com.example.assessment_service.client.QuestionEngineClient;
import com.example.assessment_service.client.ReportClient;
import com.example.assessment_service.client.TranscribeClient;
import com.example.assessment_service.dto.*;
import com.example.assessment_service.entity.*;
import com.example.assessment_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final AssessmentTemplateRepository templateRepo;
    private final AssessmentQuestionRepository questionRepo;
    private final AssessmentAttemptRepository attemptRepo;
    private final AssessmentAnswerRepository answerRepo;
    private final TranscribeClient transcribeClient;
    private final QuestionEngineClient questionClient;
    private final ReportClient reportClient;
    private final SecureRandom random = new SecureRandom();
    @Value("${app.public-base-url}")
    private String publicBaseUrl;

    @Transactional
    public AssessmentResponse create(UUID ownerHrUserId, CreateAssessmentRequest req) {
        OffsetDateTime now = OffsetDateTime.now();
        UUID templateId = UUID.randomUUID();
        AssessmentTemplateEntity t = new AssessmentTemplateEntity();
        t.setId(templateId);
        t.setOwnerHrUserId(ownerHrUserId);
        t.setTitle(req.getTitle());
        t.setDescription(req.getDescription());
        t.setProfession(req.getProfession());
        t.setLevel(req.getLevel());
        t.setPublicToken(token());
        t.setVoiceRequired(req.getVoiceRequired());
        t.setCameraRequired(req.getCameraRequired());
        t.setRecordingRequired(req.getRecordingRequired());
        t.setTextAnswersAllowed(req.getTextAnswersAllowed());
        t.setActive(true);
        t.setCreatedAt(now);
        t.setUpdatedAt(now);
        templateRepo.save(t);
        int pos = 1;
        for (CreateAssessmentRequest.QuestionDto q : req.getQuestions()) {
            AssessmentQuestionEntity e = new AssessmentQuestionEntity();
            e.setId(UUID.randomUUID());
            e.setTemplateId(templateId);
            e.setQuestionText(q.getText());
            e.setPosition(pos++);
            e.setExpectedAnswer(q.getExpectedAnswer());
            e.setSkillTag(q.getSkillTag());
            e.setRequired(true);
            e.setCreatedAt(now);
            questionRepo.save(e);
        }
        return toResponse(t);
    }

    public List<AssessmentResponse> my(UUID ownerHrUserId) {
        return templateRepo.findByOwnerHrUserIdOrderByCreatedAtDesc(ownerHrUserId).stream().map(this::toResponse).toList();
    }

    public PublicAssessmentResponse publicAssessment(String token) {
        AssessmentTemplateEntity t = findTemplateByToken(token);
        var qs = questionRepo.findByTemplateIdOrderByPositionAsc(t.getId());
        PublicAssessmentResponse r = new PublicAssessmentResponse();
        r.setTitle(t.getTitle());
        r.setDescription(t.getDescription());
        r.setProfession(t.getProfession());
        r.setLevel(t.getLevel());
        r.setVoiceRequired(t.getVoiceRequired());
        r.setCameraRequired(t.getCameraRequired());
        r.setRecordingRequired(t.getRecordingRequired());
        r.setTextAnswersAllowed(t.getTextAnswersAllowed());
        r.setQuestionsCount(qs.size());
        return r;
    }

    @Transactional
    public AttemptQuestionResponse start(String token, StartAttemptRequest req) {
        AssessmentTemplateEntity t = findTemplateByToken(token);
        var qs = questionRepo.findByTemplateIdOrderByPositionAsc(t.getId());
        if (qs.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No questions");
        AssessmentAttemptEntity a = new AssessmentAttemptEntity();
        a.setId(UUID.randomUUID());
        a.setTemplateId(t.getId());
        a.setCandidateName(req.getCandidateName());
        a.setCandidateEmail(req.getCandidateEmail());
        a.setStatus("IN_PROGRESS");
        a.setCurrentPosition(1);
        a.setStartedAt(OffsetDateTime.now());
        a.setCreatedAt(OffsetDateTime.now());
        attemptRepo.save(a);
        return questionResponse(t, a, qs.get(0), qs.size(), false, null);
    }

    @Transactional
    public SubmitAssessmentAnswerResponse answer(UUID attemptId, SubmitAssessmentAnswerRequest req) {
        return processAnswer(attemptId, req.getAnswer(), req.getInputType() == null ? "text" : req.getInputType(), null);
    }

    @Transactional
    public SubmitAssessmentAnswerResponse voice(UUID attemptId, SubmitAssessmentVoiceRequest req) {
        TranscribeClient.Response tr = transcribeClient.transcribe(req.getAudioBase64());
        if (tr == null || tr.getText() == null || tr.getText().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Transcription is empty");
        return processAnswer(attemptId, tr.getText().trim(), "voice", tr.getText().trim());
    }

    private SubmitAssessmentAnswerResponse processAnswer(UUID attemptId, String answer, String inputType, String transcribedText) {
        AssessmentAttemptEntity a = findAttempt(attemptId);
        AssessmentTemplateEntity t = templateRepo.findById(a.getTemplateId()).orElseThrow();
        var qs = questionRepo.findByTemplateIdOrderByPositionAsc(t.getId());
        if (!"IN_PROGRESS".equals(a.getStatus()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attempt is not active");
        int idx = a.getCurrentPosition() - 1;
        if (idx < 0 || idx >= qs.size())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No current question");
        AssessmentQuestionEntity q = qs.get(idx);
        var eval = questionClient.evaluateAnswer(t.getProfession(), t.getLevel(), q.getQuestionText(), answer);
        AssessmentAnswerEntity ans = new AssessmentAnswerEntity();
        ans.setId(UUID.randomUUID());
        ans.setAttemptId(a.getId());
        ans.setQuestionId(q.getId());
        ans.setQuestionText(q.getQuestionText());
        ans.setAnswerText(answer);
        ans.setInputType(inputType);
        ans.setOverallScore(bd(eval == null ? null : eval.getOverallScore()));
        ans.setCorrectnessScore(bd(eval == null ? null : eval.getCorrectnessScore()));
        ans.setCompletenessScore(bd(eval == null ? null : eval.getCompletenessScore()));
        ans.setClarityScore(bd(eval == null ? null : eval.getClarityScore()));
        ans.setRelevanceScore(bd(eval == null ? null : eval.getRelevanceScore()));
        ans.setGrammarScore(bd(eval == null ? null : eval.getGrammarScore()));
        ans.setFeedback(eval == null ? "Ответ принят." : eval.getFeedback());
        ans.setCreatedAt(OffsetDateTime.now());
        answerRepo.save(ans);
        SubmitAssessmentAnswerResponse r = new SubmitAssessmentAnswerResponse();
        r.setAttemptId(a.getId());
        r.setTranscribedText(transcribedText);
        r.setPreviousScore(eval == null ? null : eval.getOverallScore());
        r.setFeedback(ans.getFeedback());
        if (a.getCurrentPosition() >= qs.size()) {
            completeAttempt(a, t);
            r.setInterviewComplete(true);
            r.setReportId(a.getReportId());
            r.setResultPublicUrl(publicBaseUrl + "/assessment/result/" + a.getResultToken());
            r.setResultPublicToken(a.getResultToken());
            r.setMessage("Интервью завершено.");
            return r;
        }
        a.setCurrentPosition(a.getCurrentPosition() + 1);
        attemptRepo.save(a);
        AssessmentQuestionEntity next = qs.get(a.getCurrentPosition() - 1);
        r.setInterviewComplete(false);
        r.setQuestion(next.getQuestionText());
        r.setQuestionNumber(a.getCurrentPosition());
        r.setTotalQuestions(qs.size());
        r.setMessage("Вопрос " + a.getCurrentPosition() + " из " + qs.size());
        return r;
    }

    private void completeAttempt(AssessmentAttemptEntity a, AssessmentTemplateEntity t) {
        var answers = answerRepo.findByAttemptIdOrderByCreatedAtAsc(a.getId());
        double avg = answers.stream().filter(x -> x.getOverallScore() != null).mapToDouble(x -> x.getOverallScore().doubleValue()).average().orElse(0);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("profession", t.getProfession());
        payload.put("level", t.getLevel());
        payload.put("overall_score", avg);
        payload.put("answers", answers.stream().map(x -> Map.of("question", x.getQuestionText(), "answer", x.getAnswerText() == null ? "" : x.getAnswerText(), "score", x.getOverallScore() == null ? 0 : x.getOverallScore(), "feedback", x.getFeedback() == null ? "" : x.getFeedback())).toList());
        ReportClient.CreateRequest rr = new ReportClient.CreateRequest();
        rr.setSourceType("HR_ASSESSMENT");
        rr.setSourceId(a.getId());
        rr.setOwnerUserId(t.getOwnerHrUserId());
        rr.setCandidateName(a.getCandidateName());
        rr.setCandidateEmail(a.getCandidateEmail());
        rr.setOverallScore(BigDecimal.valueOf(avg));
        rr.setRecommendation(avg >= 7 ? "Можно рассматривать" : "Не рекомендуется");
        rr.setSummary("Кандидат прошел HR assessment: " + t.getTitle());
        rr.setReportPayload(payload);
        ReportClient.Response saved = reportClient.create(rr);
        a.setStatus("COMPLETED");
        a.setCompletedAt(OffsetDateTime.now());
        a.setOverallScore(BigDecimal.valueOf(avg));
        a.setRecommendation(rr.getRecommendation());
        a.setReportId(saved.getId());
        a.setResultToken(saved.getPublicToken());
        attemptRepo.save(a);
    }

    public List<AssessmentAttemptListItemResponse> getAttempts(UUID ownerHrUserId, UUID assessmentId) {
        AssessmentTemplateEntity template = templateRepo.findById(assessmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assessment not found"));

        if (!template.getOwnerHrUserId().equals(ownerHrUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Assessment does not belong to current user");
        }

        return attemptRepo.findByTemplateIdOrderByCreatedAtDesc(assessmentId)
                .stream()
                .map(this::toAttemptListItem)
                .toList();
    }

    public AssessmentAttemptDetailsResponse getAttemptDetails(UUID ownerHrUserId, UUID attemptId) {
        AssessmentAttemptEntity attempt = findAttempt(attemptId);
        AssessmentTemplateEntity template = templateRepo.findById(attempt.getTemplateId()).orElseThrow();

        if (!template.getOwnerHrUserId().equals(ownerHrUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Attempt does not belong to current user");
        }

        return toAttemptDetails(template, attempt);
    }

    public AssessmentAttemptDetailsResponse publicResult(String resultToken) {
        AssessmentAttemptEntity attempt = attemptRepo.findByResultToken(resultToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Result not found"));
        AssessmentTemplateEntity template = templateRepo.findById(attempt.getTemplateId()).orElseThrow();
        return toAttemptDetails(template, attempt);
    }

    private AssessmentAttemptListItemResponse toAttemptListItem(AssessmentAttemptEntity a) {
        AssessmentAttemptListItemResponse r = new AssessmentAttemptListItemResponse();
        r.setId(a.getId());
        r.setCandidateName(a.getCandidateName());
        r.setCandidateEmail(a.getCandidateEmail());
        r.setStatus(a.getStatus());
        r.setOverallScore(a.getOverallScore());
        r.setRecommendation(a.getRecommendation());
        r.setStartedAt(a.getStartedAt());
        r.setCompletedAt(a.getCompletedAt());
        r.setReportId(a.getReportId());
        r.setResultPublicToken(a.getResultToken());
        return r;
    }

    private AssessmentAttemptDetailsResponse toAttemptDetails(AssessmentTemplateEntity template, AssessmentAttemptEntity attempt) {
        AssessmentAttemptDetailsResponse r = new AssessmentAttemptDetailsResponse();
        r.setId(attempt.getId());
        r.setAssessmentId(template.getId());
        r.setAssessmentTitle(template.getTitle());
        r.setProfession(template.getProfession());
        r.setLevel(template.getLevel());
        r.setCandidateName(attempt.getCandidateName());
        r.setCandidateEmail(attempt.getCandidateEmail());
        r.setStatus(attempt.getStatus());
        r.setOverallScore(attempt.getOverallScore());
        r.setRecommendation(attempt.getRecommendation());
        r.setReportId(attempt.getReportId());
        r.setResultPublicToken(attempt.getResultToken());
        r.setStartedAt(attempt.getStartedAt());
        r.setCompletedAt(attempt.getCompletedAt());

        r.setAnswers(answerRepo.findByAttemptIdOrderByCreatedAtAsc(attempt.getId()).stream().map(a -> {
            AssessmentAttemptDetailsResponse.Answer x = new AssessmentAttemptDetailsResponse.Answer();
            x.setId(a.getId());
            x.setQuestionText(a.getQuestionText());
            x.setAnswerText(a.getAnswerText());
            x.setInputType(a.getInputType());
            x.setOverallScore(a.getOverallScore());
            x.setCorrectnessScore(a.getCorrectnessScore());
            x.setCompletenessScore(a.getCompletenessScore());
            x.setClarityScore(a.getClarityScore());
            x.setRelevanceScore(a.getRelevanceScore());
            x.setGrammarScore(a.getGrammarScore());
            x.setFeedback(a.getFeedback());
            x.setCreatedAt(a.getCreatedAt());
            return x;
        }).toList());

        return r;
    }

    private AssessmentTemplateEntity findTemplateByToken(String token) {
        return templateRepo.findByPublicTokenAndActiveTrue(token).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assessment not found"));
    }

    private AssessmentAttemptEntity findAttempt(UUID id) {
        return attemptRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt not found"));
    }

    private AssessmentResponse toResponse(AssessmentTemplateEntity t) {
        var qs = questionRepo.findByTemplateIdOrderByPositionAsc(t.getId());
        AssessmentResponse r = new AssessmentResponse();
        r.setId(t.getId());
        r.setOwnerHrUserId(t.getOwnerHrUserId());
        r.setTitle(t.getTitle());
        r.setDescription(t.getDescription());
        r.setProfession(t.getProfession());
        r.setLevel(t.getLevel());
        r.setPublicToken(t.getPublicToken());
        r.setPublicUrl(publicBaseUrl + "/assessment/public/" + t.getPublicToken());
        r.setVoiceRequired(t.getVoiceRequired());
        r.setCameraRequired(t.getCameraRequired());
        r.setRecordingRequired(t.getRecordingRequired());
        r.setTextAnswersAllowed(t.getTextAnswersAllowed());
        r.setQuestions(qs.stream().map(q -> {
            AssessmentResponse.Question x = new AssessmentResponse.Question();
            x.setId(q.getId());
            x.setText(q.getQuestionText());
            x.setPosition(q.getPosition());
            return x;
        }).toList());
        r.setAttemptsCount((int) attemptRepo.countByTemplateId(t.getId()));
        r.setCreatedAt(t.getCreatedAt());
        return r;
    }

    private AttemptQuestionResponse questionResponse(AssessmentTemplateEntity t, AssessmentAttemptEntity a, AssessmentQuestionEntity q, int total, boolean complete, String msg) {
        AttemptQuestionResponse r = new AttemptQuestionResponse();
        r.setAttemptId(a.getId());
        r.setQuestion(q.getQuestionText());
        r.setQuestionNumber(a.getCurrentPosition());
        r.setTotalQuestions(total);
        r.setVoiceRequired(t.getVoiceRequired());
        r.setCameraRequired(t.getCameraRequired());
        r.setRecordingRequired(t.getRecordingRequired());
        r.setInterviewComplete(complete);
        r.setMessage(msg);
        return r;
    }

    private BigDecimal bd(Double v) {
        return v == null ? null : BigDecimal.valueOf(v);
    }

    private String token() {
        byte[] b = new byte[32];
        random.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }
}
