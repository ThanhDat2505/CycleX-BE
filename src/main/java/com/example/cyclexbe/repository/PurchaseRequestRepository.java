package com.example.cyclexbe.repository;

import com.example.cyclexbe.domain.enums.PurchaseRequestStatus;
import com.example.cyclexbe.entity.PurchaseRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Integer> {

    /**
     * Find all purchase requests for a specific listing
     */
    List<PurchaseRequest> findByListing_ListingId(Integer listingId);

    /**
     * Find a purchase request by its ID
     */
    Optional<PurchaseRequest> findByRequestId(Integer requestId);

    /**
     * Find active purchase request for a listing
     * (PENDING_SELLER_CONFIRM, SELLER_CONFIRMED, BUYER_CONFIRMED)
     */
    @Query("SELECT pr FROM PurchaseRequest pr " +
           "WHERE pr.listing.listingId = :listingId " +
           "AND pr.status IN ('PENDING_SELLER_CONFIRM', 'SELLER_CONFIRMED', 'BUYER_CONFIRMED')")
    Optional<PurchaseRequest> findActiveRequestForListing(@Param("listingId") Integer listingId);

    /**
     * Find purchase requests by buyer ID
     */
    List<PurchaseRequest> findByBuyer_UserId(Integer buyerId);

    /**
     * Find purchase requests by listing and buyer
     */
    @Query("SELECT pr FROM PurchaseRequest pr " +
           "WHERE pr.listing.listingId = :listingId " +
           "AND pr.buyer.userId = :buyerId")
    List<PurchaseRequest> findByListingAndBuyer(
            @Param("listingId") Integer listingId,
            @Param("buyerId") Integer buyerId);

    /**
     * Check if a buyer has an active request for a listing
     */
    @Query("SELECT COUNT(pr) > 0 FROM PurchaseRequest pr " +
           "WHERE pr.listing.listingId = :listingId " +
           "AND pr.buyer.userId = :buyerId " +
           "AND pr.status IN ('PENDING_SELLER_CONFIRM', 'SELLER_CONFIRMED', 'BUYER_CONFIRMED')")
    boolean existsActiveRequestForBuyer(
            @Param("listingId") Integer listingId,
            @Param("buyerId") Integer buyerId);
}

