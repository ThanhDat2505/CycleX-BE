package com.example.cyclexbe.repository;

import com.example.cyclexbe.entity.InspectionRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectionRequirementRepository extends JpaRepository<InspectionRequirement, Integer> {

    // InspectionRequirementRepository (đúng theo BR: chỉ unresolved)
    List<InspectionRequirement> findByInspectionRequest_RequestIdAndResolvedFalse(Integer requestId);

    // nếu bạn còn dùng chỗ khác
    List<InspectionRequirement> findByInspectionRequest_RequestId(Integer requestId);
}

