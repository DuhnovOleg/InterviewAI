package com.example.interviewservice.service;

import com.example.interviewservice.component.AIClient;
import com.example.interviewservice.dto.AnswerRequest;
import com.example.interviewservice.dto.StartInterviewRequest;
import com.example.interviewservice.dto.StartInterviewResponse;
import com.example.interviewservice.entity.Interview;
import com.example.interviewservice.entity.Question;
import com.example.interviewservice.mapper.AnswerMapper;
import com.example.interviewservice.mapper.InterviewMapper;
import com.example.interviewservice.mapper.QuestionMapper;
import com.example.interviewservice.repository.AnswerRepository;
import com.example.interviewservice.repository.InterviewRepository;
import com.example.interviewservice.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    private final InterviewMapper interviewMapper;
    private final QuestionMapper questionMapper;
    private final AnswerMapper answerMapper;

    private final AIClient aiClient;

    public StartInterviewResponse startInterview(StartInterviewRequest request) {
        Interview interview = interviewMapper.fromStartRequest(request);
        interviewRepository.save(interview);

        aiClient.classifyIntent(request.initialPrompt());

        String questionText = aiClient.generateQuestion(interview.getId(), null);

        Question question = questionMapper.toQuestion(
                interview,
                questionText,
                1
        );

        questionRepository.save(question);

        return new StartInterviewResponse(
                interview.getId(),
                questionText
        );
    }

    public void submitAnswer(String id, AnswerRequest request) {

    }

}
