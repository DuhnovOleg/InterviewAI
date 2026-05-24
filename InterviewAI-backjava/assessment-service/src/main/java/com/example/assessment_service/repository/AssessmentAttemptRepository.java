package com.example.assessment_service.repository;

import com.example.assessment_service.entity.AssessmentAttemptEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssessmentAttemptRepository extends org.springframework.data.jpa.repository.JpaRepository<AssessmentAttemptEntity, UUID> {

    List<AssessmentAttemptEntity> findByTemplateIdOrderByCreatedAtDesc(UUID templateId);

    long countByTemplateId(UUID templateId);

    Optional<AssessmentAttemptEntity> findByResultToken(String resultToken);

}
