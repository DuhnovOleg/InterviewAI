package com.example.reportservice.repository;

import com.example.reportservice.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<ReportEntity, UUID> {

    Optional<ReportEntity> findByPublicToken(String publicToken);
    List<ReportEntity> findByOwnerUserIdOrderByCreatedAtDesc(UUID ownerUserId);
    List<ReportEntity> findBySourceTypeAndSourceId(String sourceType, UUID sourceId);

}
