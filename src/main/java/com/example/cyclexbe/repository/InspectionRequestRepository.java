package com.example.cyclexbe.repository;

import com.example.cyclexbe.entity.InspectionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InspectionRequestRepository extends JpaRepository<InspectionRequest, Integer> {
    Optional<InspectionRequest> findByRequestId(Integer requestId);
}

