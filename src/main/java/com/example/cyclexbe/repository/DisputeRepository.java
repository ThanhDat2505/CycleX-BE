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

import java.time.LocalDateTime;

@Repository
public interface DisputeRepository extends JpaRepository<Dispute, Integer> {

    Page<Dispute> findByStatus(DisputeStatus status, Pageable pageable);

    @Query("SELECT d FROM Dispute d WHERE " +
            "(:status IS NULL OR d.status = :status) AND " +
            "(:search IS NULL OR LOWER(d.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR CAST(d.disputeId AS string) = :search) AND " +
            "(:fromDate IS NULL OR d.createdAt >= :fromDate) AND " +
            "(:toDate IS NULL OR d.createdAt <= :toDate)")
    Page<Dispute> findByFilters(
            @Param("status") DisputeStatus status,
            @Param("search") String search,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);

    @Query("SELECT d FROM Dispute d WHERE " +
            "(:status IS NULL OR d.status = :status) AND " +
            "(:search IS NULL OR LOWER(d.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR CAST(d.disputeId AS string) = :search)")
    Page<Dispute> findByFilters(
            @Param("status") DisputeStatus status,
            @Param("search") String search,
            Pageable pageable);

    long countByStatus(DisputeStatus status);

    long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    long countByAssignee(User assignee);

    long countByAssigneeAndStatusIn(User assignee, java.util.List<DisputeStatus> statuses);

    boolean existsByPurchaseRequest_RequestId(Integer requestId);

    @Query("SELECT d FROM Dispute d WHERE d.requester.userId = :buyerId AND d.purchaseRequest.requestId = :requestId")
    Dispute findByRequesterAndPurchaseRequest(@Param("buyerId") Integer buyerId, @Param("requestId") Integer requestId);

    @Query("SELECT d FROM Dispute d WHERE d.assignee.userId = :assigneeId AND " +
            "(:status IS NULL OR d.status = :status) AND " +
            "(:search IS NULL OR LOWER(d.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR CAST(d.disputeId AS string) = :search) AND " +
            "(:fromDate IS NULL OR d.createdAt >= :fromDate) AND " +
            "(:toDate IS NULL OR d.createdAt <= :toDate)")
    Page<Dispute> findByAssigneeAndFilters(
            @Param("assigneeId") Integer assigneeId,
            @Param("status") DisputeStatus status,
            @Param("search") String search,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);

    @Query("SELECT d FROM Dispute d WHERE d.assignee.userId = :assigneeId AND " +
            "(:status IS NULL OR d.status = :status) AND " +
            "(:search IS NULL OR LOWER(d.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR CAST(d.disputeId AS string) = :search)")
    Page<Dispute> findByAssigneeAndFilters(
            @Param("assigneeId") Integer assigneeId,
            @Param("status") DisputeStatus status,
            @Param("search") String search,
            Pageable pageable);

    @Query("SELECT d FROM Dispute d WHERE d.requester.userId = :buyerId")
    Page<Dispute> findByRequesterId(@Param("buyerId") Integer buyerId, Pageable pageable);
}
