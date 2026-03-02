package com.example.cyclexbe.repository;

import com.example.cyclexbe.entity.BikeListing;
import com.example.cyclexbe.entity.InspectionReport;
import com.example.cyclexbe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InspectionReportRepository extends JpaRepository<InspectionReport, Integer> {

    List<InspectionReport> findByListingOrderByCreatedAtDesc(BikeListing listing);

    List<InspectionReport> findByInspectorOrderByCreatedAtDesc(User inspector);

    Optional<InspectionReport> findTopByListingOrderByCreatedAtDesc(BikeListing listing);

    List<InspectionReport> findByListingAndDecision(BikeListing listing, String decision);
}
