package com.example.cyclexbe.repository;

import com.example.cyclexbe.domain.enums.OrderStatus;
import com.example.cyclexbe.domain.enums.TransactionType;
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

    Optional<Order> findByProduct_ProductId(Integer productId);

    @Query("""
            SELECT o FROM Order o
            WHERE o.seller.userId = :sellerId
            AND o.status = com.example.cyclexbe.domain.enums.OrderStatus.PENDING_SELLER_CONFIRM
            AND (:type IS NULL OR o.transactionType = :type)
            AND (
                LOWER(o.buyer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(o.product.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            """)
    Page<Order> findPendingOrdersForSeller(
            @Param("sellerId") Integer sellerId,
            @Param("type") TransactionType type,
            @Param("keyword") String keyword,
            Pageable pageable);

    Optional<Order> findByOrderIdAndSeller_UserId(Integer orderId, Integer sellerId);

    @Query("SELECT COUNT(o) > 0 FROM Order o WHERE o.product.productId = :productId AND o.status IN ('PENDING_SELLER_CONFIRM', 'PENDING_DELIVERY', 'IN_DELIVERY')")
    boolean existsActiveOrderForProduct(@Param("productId") Integer productId);
}
