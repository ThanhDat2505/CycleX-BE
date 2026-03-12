package com.example.cyclexbe.repository;

import com.example.cyclexbe.domain.enums.OrderStatus;
import com.example.cyclexbe.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    Optional<Order> findByPurchaseRequest_RequestId(Integer requestId);

    Page<Order> findByBuyer_UserId(Integer buyerId, Pageable pageable);

    Page<Order> findBySeller_UserId(Integer sellerId, Pageable pageable);

    Page<Order> findByBuyer_UserIdAndStatus(Integer buyerId, OrderStatus status, Pageable pageable);

    Page<Order> findBySeller_UserIdAndStatus(Integer sellerId, OrderStatus status, Pageable pageable);

    long countByStatus(OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.buyer.userId = :userId OR o.seller.userId = :userId")
    Page<Order> findByBuyerOrSeller(@Param("userId") Integer userId, Pageable pageable);
}
