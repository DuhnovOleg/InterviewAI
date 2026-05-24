package com.example.assessment_service.repository;

import com.example.assessment_service.entity.MediaFileEntity;

import java.util.List;
import java.util.UUID;

public interface MediaFileRepository extends org.springframework.data.jpa.repository.JpaRepository<MediaFileEntity, UUID> {

    List<MediaFileEntity> findByAttemptId(UUID attemptId);

}
