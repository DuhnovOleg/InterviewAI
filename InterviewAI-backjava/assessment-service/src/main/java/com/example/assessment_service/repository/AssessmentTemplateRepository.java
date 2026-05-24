package com.example.assessment_service.repository;

import com.example.assessment_service.entity.AssessmentTemplateEntity;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface AssessmentTemplateRepository extends org.springframework.data.jpa.repository.JpaRepository<AssessmentTemplateEntity, UUID> {

    Optional<AssessmentTemplateEntity> findByPublicTokenAndActiveTrue(String publicToken);

    List<AssessmentTemplateEntity> findByOwnerHrUserIdOrderByCreatedAtDesc(UUID ownerHrUserId);

}
