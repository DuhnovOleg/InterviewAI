package com.example.interviewservice.service;

import com.example.interviewservice.client.QuestionClient;
import com.example.interviewservice.client.TranscribeClient;
import com.example.interviewservice.dto.python.*;
import com.example.interviewservice.dto.request.*;
import com.example.interviewservice.dto.response.FinalEvaluationResponse;
import com.example.interviewservice.dto.response.SessionStatusResponse;
import com.example.interviewservice.dto.response.StartInterviewResponse;
import com.example.interviewservice.dto.response.SubmitAnswerResponse;
import com.example.interviewservice.entity.InterviewSessionEntity;
import com.example.interviewservice.exception.ExternalServiceException;
import com.example.interviewservice.exception.SessionNotFoundException;
import com.example.interviewservice.kafka.InterviewEventProducer;
import com.example.interviewservice.kafka.event.InterviewAnswerSubmittedEvent;
import com.example.interviewservice.kafka.event.InterviewCompletedEvent;
import com.example.interviewservice.kafka.event.InterviewStartedEvent;
import com.example.interviewservice.kafka.event.VoiceAnswerSubmittedEvent;
import com.example.interviewservice.model.GuestInterviewState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final QuestionClient questionEngineClient;
    private final TranscribeClient transcribeClient;
    private final AuthenticatedInterviewPersistenceService persistenceService;
    private final GuestSessionStore guestSessionStore;
    private final UserContextService userContextService;
    private final InterviewEventProducer eventProducer;

    @Transactional
    public StartInterviewResponse startInterview(StartInterviewRequest request) {
        boolean authenticated = userContextService.isAuthenticated();
        UUID userId = userContextService.getCurrentUserIdOrNull();

        QuestionStartResponse python = questionEngineClient.startInterview(
                new QuestionStartRequest(request.getMessage())
        );

        String localSessionId = UUID.randomUUID().toString();

        if (authenticated && userId != null) {
            InterviewSessionEntity entity = persistenceService.createSession(
                    userId,
                    python.getSessionId(),
                    python.getProfession(),
                    python.getLevel(),
                    python.getTotalQuestions()
            );
            localSessionId = entity.getId().toString();
        } else {
            guestSessionStore.save(localSessionId, GuestInterviewState.builder()
                    .pythonSessionId(python.getSessionId())
                    .profession(python.getProfession())
                    .level(python.getLevel())
                    .totalQuestions(python.getTotalQuestions())
                    .awaitingStopConfirmation(false)
                    .build());
        }

        eventProducer.publishInterviewStarted(
                InterviewStartedEvent.builder()
                        .localSessionId(localSessionId)
                        .pythonSessionId(python.getSessionId())
                        .userId(userId)
                        .authenticated(authenticated)
                        .profession(python.getProfession())
                        .level(python.getLevel())
                        .totalQuestions(python.getTotalQuestions())
                        .createdAt(OffsetDateTime.now())
                        .build()
        );

        StartInterviewResponse response = new StartInterviewResponse();
        response.setSessionId(localSessionId);
        response.setProfession(python.getProfession());
        response.setLevel(python.getLevel());
        response.setTotalQuestions(python.getTotalQuestions());
        response.setQuestion(python.getQuestion());
        response.setQuestionNumber(python.getQuestionNumber());
        response.setMessage(python.getMessage());
        response.setRegisteredUser(authenticated);
        return response;
    }

    @Transactional
    public SubmitAnswerResponse submitAnswer(SubmitAnswerRequest request) {
        SessionMapping mapping = resolveSession(request.getSessionId());

        QuestionAnswerRequest pythonRequest = new QuestionAnswerRequest();
        pythonRequest.setSessionId(mapping.pythonSessionId());
        pythonRequest.setAnswer(request.getAnswer());
        pythonRequest.setInputType(request.getInputType());
        pythonRequest.setConfidenceScore(request.getConfidenceScore());
        pythonRequest.setResponseTimeSeconds(request.getResponseTimeSeconds());

        QuestionAnswerResponse python = questionEngineClient.submitAnswer(pythonRequest);

        if (mapping.authenticated() && mapping.localSessionEntity() != null) {
            persistenceService.saveAnswer(
                    mapping.localSessionEntity().getId(),
                    python.getQuestionNumber() != null ? python.getQuestionNumber() - 1 : null,
                    null,
                    request.getAnswer(),
                    request.getInputType(),
                    python.getPreviousScore(),
                    python.getFeedback()
            );

            if (Boolean.TRUE.equals(python.getInterviewComplete())) {
                persistenceService.markCompleted(mapping.localSessionEntity().getId());
            }
        }

        eventProducer.publishAnswerSubmitted(
                InterviewAnswerSubmittedEvent.builder()
                        .localSessionId(request.getSessionId())
                        .pythonSessionId(mapping.pythonSessionId())
                        .inputType(request.getInputType())
                        .createdAt(OffsetDateTime.now())
                        .build()
        );

        SubmitAnswerResponse response = new SubmitAnswerResponse();
        response.setSessionId(request.getSessionId());
        response.setQuestion(python.getQuestion());
        response.setQuestionNumber(python.getQuestionNumber());
        response.setTotalQuestions(python.getTotalQuestions());
        response.setInterviewComplete(python.getInterviewComplete());
        response.setAwaitingStopConfirmation(python.getAwaitingStopConfirmation());
        response.setPreviousScore(python.getPreviousScore());
        response.setFeedback(python.getFeedback());
        response.setStrengths(python.getStrengths());
        response.setWeaknesses(python.getWeaknesses());
        response.setMessage(python.getMessage());

        if (Boolean.TRUE.equals(python.getInterviewComplete())) {
            Object finalReport = questionEngineClient.finalEvaluation(
                    new QuestionFinalEvaluationRequest(mapping.pythonSessionId())
            );

            if (mapping.authenticated() && mapping.localSessionEntity() != null) {
                persistenceService.saveReport(mapping.localSessionEntity().getId(), finalReport);
            }

            eventProducer.publishInterviewCompleted(
                    InterviewCompletedEvent.builder()
                            .localSessionId(request.getSessionId())
                            .pythonSessionId(mapping.pythonSessionId())
                            .averageScore(python.getAverageScore())
                            .recommendation(python.getRecommendation())
                            .createdAt(OffsetDateTime.now())
                            .build()
            );

            response.setFinalPayload(Map.of("report", finalReport));
        }

        return response;
    }

    @Transactional
    public SubmitAnswerResponse submitVoiceAnswer(SubmitVoiceAnswerRequest request) {
        SessionMapping mapping = resolveSession(request.getSessionId());

        TranscribeRequest transcribeRequest = new TranscribeRequest();
        transcribeRequest.setAudioBase64(request.getAudioBase64());
        transcribeRequest.setLanguageHint("ru");

        TranscribeResponse transcribed = transcribeClient.transcribe(transcribeRequest);

        if (transcribed == null || transcribed.getText() == null || transcribed.getText().isBlank()) {
            throw new ExternalServiceException("Transcribe service returned empty text", null);
        }

        eventProducer.publishVoiceAnswerSubmitted(
                VoiceAnswerSubmittedEvent.builder()
                        .localSessionId(request.getSessionId())
                        .pythonSessionId(mapping.pythonSessionId())
                        .createdAt(OffsetDateTime.now())
                        .build()
        );

        SubmitAnswerRequest answerRequest = new SubmitAnswerRequest();
        answerRequest.setSessionId(request.getSessionId());
        answerRequest.setAnswer(transcribed.getText().trim());
        answerRequest.setInputType("voice");

        Double effectiveResponseTime = request.getResponseTimeSeconds();
        if (effectiveResponseTime == null) {
            effectiveResponseTime = transcribed.getDurationSeconds();
        }
        answerRequest.setResponseTimeSeconds(effectiveResponseTime);

        SubmitAnswerResponse response = submitAnswer(answerRequest);
        response.setTranscribedText(transcribed.getText().trim());

        return response;
    }

    public FinalEvaluationResponse finalEvaluation(FinalEvaluationRequest request) {
        SessionMapping mapping = resolveSession(request.getSessionId());
        Object report = questionEngineClient.finalEvaluation(
                new QuestionFinalEvaluationRequest(mapping.pythonSessionId())
        );

        if (mapping.authenticated() && mapping.localSessionEntity() != null) {
            persistenceService.saveReport(mapping.localSessionEntity().getId(), report);
        }

        FinalEvaluationResponse response = new FinalEvaluationResponse();
        response.setSessionId(request.getSessionId());
        response.setReport((Map<String, Object>) report);
        return response;
    }

    public SessionStatusResponse getStatus(String sessionId) {
        boolean authenticated = userContextService.isAuthenticated();

        SessionStatusResponse response = new SessionStatusResponse();
        response.setSessionId(sessionId);

        if (authenticated) {
            UUID localId = UUID.fromString(sessionId);
            InterviewSessionEntity entity = persistenceService.findByPythonSessionId(localId.toString());
            if (entity == null) {
                throw new SessionNotFoundException("Interview session not found");
            }
            response.setProfession(entity.getProfession());
            response.setLevel(entity.getLevel());
            response.setComplete("COMPLETED".equals(entity.getStatus()));
            response.setAwaitingStopConfirmation(entity.getAwaitingStopConfirmation());
            response.setTotalQuestions(entity.getTotalQuestions());
        } else {
            GuestInterviewState state = guestSessionStore.get(sessionId);
            if (state == null) {
                throw new SessionNotFoundException("Guest interview session not found");
            }
            response.setProfession(state.getProfession());
            response.setLevel(state.getLevel());
            response.setComplete(false);
            response.setAwaitingStopConfirmation(state.getAwaitingStopConfirmation());
            response.setTotalQuestions(state.getTotalQuestions());
        }

        return response;
    }

    public SubmitAnswerResponse stopInterview(StopInterviewRequest request) {
        SubmitAnswerRequest stop = new SubmitAnswerRequest();
        stop.setSessionId(request.getSessionId());
        stop.setAnswer("давай остановимся");
        stop.setInputType("text");
        return submitAnswer(stop);
    }

    private SessionMapping resolveSession(String localSessionId) {
        boolean authenticated = userContextService.isAuthenticated();

        if (authenticated) {
            UUID localId = UUID.fromString(localSessionId);
            InterviewSessionEntity entity = persistenceService.findByLocalSessionId(localId);
            if (entity == null) {
                throw new SessionNotFoundException("Authenticated interview session not found");
            }
            return new SessionMapping(entity.getPythonSessionId(), true, entity);
        }

        GuestInterviewState guest = guestSessionStore.get(localSessionId);
        if (guest == null) {
            throw new SessionNotFoundException("Guest interview session not found");
        }
        return new SessionMapping(guest.getPythonSessionId(), false, null);
    }

    private record SessionMapping(
            String pythonSessionId,
            boolean authenticated,
            InterviewSessionEntity localSessionEntity
    ) {
    }

}
