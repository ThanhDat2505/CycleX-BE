package com.example.cyclexbe.repository;

import com.example.cyclexbe.domain.enums.AuditLogAction;
import com.example.cyclexbe.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("SELECT a FROM AuditLog a WHERE " +
            "(:actionType IS NULL OR a.actionType = :actionType) AND " +
            "(:adminId IS NULL OR a.adminId = :adminId) AND " +
            "(:fromDate IS NULL OR a.createdAt >= :fromDate) AND " +
            "(:toDate IS NULL OR a.createdAt <= :toDate)")
    Page<AuditLog> findByFilters(
            @Param("actionType") AuditLogAction actionType,
            @Param("adminId") Integer adminId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
}
