package com.example.cyclexbe.repository;

import com.example.cyclexbe.entity.InspectionReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InspectionReportRepository extends JpaRepository<InspectionReport, Integer> {
    Optional<InspectionReport> findByListing_ListingId(Integer listingId);
}
