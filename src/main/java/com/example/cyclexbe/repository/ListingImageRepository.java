package com.example.cyclexbe.repository;

import com.example.cyclexbe.entity.BikeListing;
import com.example.cyclexbe.entity.ListingImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ListingImageRepository extends JpaRepository<ListingImage, Integer> {
    List<ListingImage> findByBikeListing(BikeListing bikeListing);
    List<ListingImage> findByBikeListingOrderByImageOrder(BikeListing bikeListing);
    Optional<ListingImage> findByImageIdAndBikeListing(Integer imageId, BikeListing bikeListing);
    long countByBikeListing(BikeListing bikeListing);
}
