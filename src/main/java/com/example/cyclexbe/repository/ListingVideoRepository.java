package com.example.cyclexbe.repository;

import com.example.cyclexbe.entity.BikeListing;
import com.example.cyclexbe.entity.ListingVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ListingVideoRepository extends JpaRepository<ListingVideo, Integer> {
    Optional<ListingVideo> findByBikeListing(BikeListing bikeListing);

    Optional<ListingVideo> findByVideoIdAndBikeListing(Integer videoId, BikeListing bikeListing);

    void deleteByBikeListing(BikeListing bikeListing);
}
