package com.example.cyclexbe.repository;

import com.example.cyclexbe.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Integer> {

    List<UserAddress> findByUserUserIdOrderByIsDefaultDescCreatedAtDesc(Integer userId);

    Optional<UserAddress> findByAddressIdAndUserUserId(Integer addressId, Integer userId);

    Optional<UserAddress> findByUserUserIdAndIsDefaultTrue(Integer userId);

    long countByUserUserId(Integer userId);
}
