package com.example.assessment_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "assessment_media_files")
public class MediaFileEntity {

    @Id
    private UUID id;
    @Column(name = "attempt_id", nullable = false)
    private UUID attemptId;
    @Column(name = "answer_id")
    private UUID answerId;
    @Column(name = "media_type", nullable = false)
    private String mediaType;
    @Column(name = "storage_key", nullable = false, columnDefinition = "text")
    private String storageKey;
    @Column(name = "original_filename")
    private String originalFilename;
    @Column(name = "mime_type")
    private String mimeType;
    @Column(name = "size_bytes")
    private Long sizeBytes;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

}
