package com.example.cyclexbe.repository;

import com.example.cyclexbe.entity.BikeListing;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.domain.enums.BikeListingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BikeListingRepository extends JpaRepository<BikeListing, Integer> {
    List<BikeListing> findBySeller(User seller);
    List<BikeListing> findByStatus(BikeListingStatus status);
    List<BikeListing> findByLocationCityContainingIgnoreCase(String city);
    List<BikeListing> findByTitleContainingIgnoreCase(String keyword);

    // Page-based queries for efficient pagination in DB
    Page<BikeListing> findByStatus(BikeListingStatus status, Pageable pageable);
    Page<BikeListing> findByLocationCityContainingIgnoreCase(String city, Pageable pageable);
    Page<BikeListing> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    Optional<BikeListing> findByListingIdAndSeller(Integer listingId, User seller);
}
