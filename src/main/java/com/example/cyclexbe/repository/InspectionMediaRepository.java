package com.example.cyclexbe.repository;

import com.example.cyclexbe.entity.InspectionMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InspectionMediaRepository extends JpaRepository<InspectionMedia, Integer> {
    List<InspectionMedia> findByInspection_InspectionIdAndCategoryOrderByUploadedAtAsc(Integer inspectionId, String category);
}
