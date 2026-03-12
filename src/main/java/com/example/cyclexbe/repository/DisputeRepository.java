package com.example.cyclexbe.repository;

import com.example.cyclexbe.domain.enums.DisputeStatus;
import com.example.cyclexbe.entity.Dispute;
import com.example.cyclexbe.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DisputeRepository extends JpaRepository<Dispute, Integer> {

    Page<Dispute> findByStatus(DisputeStatus status, Pageable pageable);

    @Query("SELECT d FROM Dispute d WHERE " +
            "(:status IS NULL OR d.status = :status) AND " +
            "(:search IS NULL OR LOWER(d.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR CAST(d.disputeId AS string) = :search)")
    Page<Dispute> findByFilters(
            @Param("status") DisputeStatus status,
            @Param("search") String search,
            Pageable pageable);

    long countByStatus(DisputeStatus status);

    long countByAssignee(User assignee);

    boolean existsByPurchaseRequest_RequestId(Integer requestId);

    @Query("SELECT d FROM Dispute d WHERE d.requester.userId = :buyerId AND d.purchaseRequest.requestId = :requestId")
    Dispute findByRequesterAndPurchaseRequest(@Param("buyerId") Integer buyerId, @Param("requestId") Integer requestId);
}
