package com.example.interviewservice.repository;

import com.example.interviewservice.entity.InterviewSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InterviewSessionRepository extends JpaRepository<InterviewSessionEntity, UUID> {
    Optional<InterviewSessionEntity> findByPythonSessionId(String pythonSessionId);
    List<InterviewSessionEntity> findTop10ByUserIdOrderByStartedAtDesc(UUID userId);
}
