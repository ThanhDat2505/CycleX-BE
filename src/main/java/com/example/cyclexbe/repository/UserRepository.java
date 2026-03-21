package com.example.cyclexbe.repository;

import com.example.cyclexbe.domain.enums.Role;
import com.example.cyclexbe.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // Inspector assignment queries
    List<User> findByRoleAndStatus(Role role, String status);

    // Admin queries
    long countByStatus(String status);
    long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT u FROM User u WHERE " +
            "(:search IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:status IS NULL OR u.status = :status)")
    Page<User> findByAdminFilters(
            @Param("search") String search,
            @Param("role") Role role,
            @Param("status") String status,
            Pageable pageable);
}
