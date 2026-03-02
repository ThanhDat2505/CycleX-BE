package com.example.cyclexbe.repository;

import com.example.cyclexbe.domain.enums.Role;
import com.example.cyclexbe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // Inspector assignment queries
    List<User> findByRoleAndStatus(Role role, String status);
}
