package com.example.cyclexbe.repository;

import com.example.cyclexbe.entity.InspectionChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectionChatMessageRepository extends JpaRepository<InspectionChatMessage, Integer> {
    List<InspectionChatMessage> findByChatThread_ThreadIdOrderByCreatedAtAsc(Integer threadId);
    Page<InspectionChatMessage> findByChatThread_ThreadIdOrderByCreatedAtDesc(Integer threadId, Pageable pageable);
}

