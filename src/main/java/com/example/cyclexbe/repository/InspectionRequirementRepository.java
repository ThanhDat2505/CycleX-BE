package com.example.cyclexbe.repository;

import com.example.cyclexbe.entity.InspectionRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectionRequirementRepository extends JpaRepository<InspectionRequirement, Integer> {

    // Lấy danh sách requirements cho 1 inspection request
    List<InspectionRequirement> findByInspectionRequest_RequestId(Integer requestId);

    // Lấy danh sách requirements chưa resolved
    List<InspectionRequirement> findByInspectionRequest_RequestIdAndResolvedFalse(Integer requestId);
}

