package com.example.assessment_service.repository;

import com.example.assessment_service.entity.AssessmentAnswerEntity;

import java.util.List;
import java.util.UUID;

public interface AssessmentAnswerRepository extends org.springframework.data.jpa.repository.JpaRepository<AssessmentAnswerEntity, UUID> {

    List<AssessmentAnswerEntity> findByAttemptIdOrderByCreatedAtAsc(UUID attemptId);

}
