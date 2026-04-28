package com.example.interviewservice.repository;

import com.example.interviewservice.entity.InterviewAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InterviewAnswerRepository extends JpaRepository<InterviewAnswerEntity, UUID> {
}
