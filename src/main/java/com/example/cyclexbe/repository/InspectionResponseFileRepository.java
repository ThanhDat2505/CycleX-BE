package com.example.cyclexbe.repository;

import com.example.cyclexbe.entity.InspectionResponseFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InspectionResponseFileRepository extends JpaRepository<InspectionResponseFile, Integer> {

    // InspectionResponseFileRepository
    List<InspectionResponseFile> findByInspectionResponse_InspectionRequest_RequestIdAndStatus(Integer requestId, String status);

    long countByInspectionResponse_InspectionRequest_RequestIdAndStatus(Integer requestId, String status);

    Optional<InspectionResponseFile> findByFileIdAndInspectionResponse_InspectionRequest_RequestId(Integer fileId, Integer requestId);
    long countByInspectionResponse_InspectionRequest_RequestIdAndStatusAndRequirement_RequirementId(
            Integer requestId, String status, Integer requirementId);

    List<InspectionResponseFile> findByInspectionResponse_InspectionRequest_RequestIdAndStatusAndRequirement_RequirementId(
            Integer requestId, String status, Integer requirementId);
}





