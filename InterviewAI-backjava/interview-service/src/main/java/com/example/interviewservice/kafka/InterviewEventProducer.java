package com.example.interviewservice.kafka;

import com.example.interviewservice.kafka.event.InterviewAnswerSubmittedEvent;
import com.example.interviewservice.kafka.event.InterviewCompletedEvent;
import com.example.interviewservice.kafka.event.InterviewStartedEvent;
import com.example.interviewservice.kafka.event.VoiceAnswerSubmittedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterviewEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${topics.interview-started}")
    private String interviewStartedTopic;

    @Value("${topics.interview-answer-submitted}")
    private String interviewAnswerSubmittedTopic;

    @Value("${topics.interview-completed}")
    private String interviewCompletedTopic;

    @Value("${topics.voice-answer-submitted}")
    private String voiceAnswerSubmittedTopic;

    public void publishInterviewStarted(InterviewStartedEvent event) {
        kafkaTemplate.send(interviewStartedTopic, event.getLocalSessionId(), event);
    }

    public void publishAnswerSubmitted(InterviewAnswerSubmittedEvent event) {
        kafkaTemplate.send(interviewAnswerSubmittedTopic, event.getLocalSessionId(), event);
    }

    public void publishInterviewCompleted(InterviewCompletedEvent event) {
        kafkaTemplate.send(interviewCompletedTopic, event.getLocalSessionId(), event);
    }

    public void publishVoiceAnswerSubmitted(VoiceAnswerSubmittedEvent event) {
        kafkaTemplate.send(voiceAnswerSubmittedTopic, event.getLocalSessionId(), event);
    }

}
