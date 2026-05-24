package com.example.assessment_service.repository;

import com.example.assessment_service.entity.AssessmentQuestionEntity;

import java.util.List;
import java.util.UUID;

public interface AssessmentQuestionRepository extends org.springframework.data.jpa.repository.JpaRepository<AssessmentQuestionEntity, UUID> {

    List<AssessmentQuestionEntity> findByTemplateIdOrderByPositionAsc(UUID templateId);

}
