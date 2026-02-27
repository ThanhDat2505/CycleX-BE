package com.example.cyclexbe.repository;

import com.example.cyclexbe.domain.enums.PurchaseRequestStatus;
import com.example.cyclexbe.domain.enums.TransactionType;
import com.example.cyclexbe.entity.PurchaseRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * Find pending transactions for a seller (S-52)
     * - Only transactions with status PENDING_SELLER_CONFIRM
     * - Only transactions for listings owned by the seller
     * - Optional filter by transaction type
     * - Optional keyword search in buyer name or listing title
     */
    @Query("""
SELECT pr FROM PurchaseRequest pr
WHERE pr.listing.seller.userId = :sellerId
AND pr.status = com.example.cyclexbe.domain.enums.PurchaseRequestStatus.PENDING_SELLER_CONFIRM
AND (:type IS NULL OR pr.transactionType = :type)
AND (
      LOWER(pr.buyer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
      OR LOWER(pr.listing.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
)
""")
    Page<PurchaseRequest> findPendingTransactionsForSeller(
            @Param("sellerId") Integer sellerId,
            @Param("type") TransactionType type,
            @Param("keyword") String keyword,
            Pageable pageable);
    //S53 - Find transaction detail for seller
    Optional<PurchaseRequest>
    findByRequestIdAndListing_Seller_UserId(Integer requestId, Integer sellerId);
}

