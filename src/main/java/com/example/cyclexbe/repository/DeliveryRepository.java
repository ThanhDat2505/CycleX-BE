package com.example.cyclexbe.repository;

import com.example.cyclexbe.entity.Delivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for Delivery entity
 * Handles shipper delivery management and dashboard queries
 */
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {

    /**
     * Find all deliveries for a specific shipper with status
     * Uses EntityGraph to avoid N+1 queries without JOIN FETCH
     */
    @EntityGraph(attributePaths = {"transaction", "listing", "listing.seller"})
    Page<Delivery> findByShipper_UserIdAndStatus(
            Integer shipperId,
            String status,
            Pageable pageable);

    /**
     * Count deliveries by shipper and status
     */
    @Query("SELECT COUNT(d) FROM Delivery d " +
           "WHERE d.shipper.userId = :shipperId " +
           "AND d.status = :status")
    long countByShipperAndStatus(
            @Param("shipperId") Integer shipperId,
            @Param("status") String status);
}

