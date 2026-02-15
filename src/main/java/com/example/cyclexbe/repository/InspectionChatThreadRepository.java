package com.example.cyclexbe.repository;

import com.example.cyclexbe.entity.InspectionChatThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InspectionChatThreadRepository extends JpaRepository<InspectionChatThread, Integer> {
    Optional<InspectionChatThread> findByInspectionRequest_RequestId(Integer requestId);
    Optional<InspectionChatThread> findByThreadId(Integer threadId);
}

