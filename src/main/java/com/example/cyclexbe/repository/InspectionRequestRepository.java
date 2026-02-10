package com.example.cyclexbe.repository;

import com.example.cyclexbe.entity.InspectionRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InspectionRequestRepository extends JpaRepository<InspectionRequest, Integer> {
    List<InspectionRequest> findBySeller_UserIdOrderByCreatedAtDesc(Integer sellerId);
    List<InspectionRequest> findByInspector_UserIdOrderByCreatedAtDesc(Integer inspectorId);
    Optional<InspectionRequest> findByListing_ListingId(Integer listingId);
}
