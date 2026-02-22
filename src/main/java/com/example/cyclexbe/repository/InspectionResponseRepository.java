package com.example.cyclexbe.repository;

import com.example.cyclexbe.entity.InspectionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InspectionResponseRepository extends JpaRepository<InspectionResponse, Integer> {

    // Tìm response theo inspection request ID
// InspectionResponseRepository
    Optional<InspectionResponse> findByInspectionRequest_RequestId(Integer requestId);}

