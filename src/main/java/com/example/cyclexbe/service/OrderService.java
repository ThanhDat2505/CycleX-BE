package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.OrderStatus;
import com.example.cyclexbe.dto.OrderResponse;
import com.example.cyclexbe.entity.Order;
import com.example.cyclexbe.entity.PurchaseRequest;
import com.example.cyclexbe.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Create an Order from a confirmed PurchaseRequest.
     * Called when seller confirms the purchase request.
     */
    @Transactional
    public Order createOrderFromPurchaseRequest(PurchaseRequest purchaseRequest, String sellerNote) {
        // Check if order already exists for this request
        if (orderRepository.findByPurchaseRequest_RequestId(purchaseRequest.getRequestId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order already exists for this purchase request");
        }

        Order order = new Order();
        order.setPurchaseRequest(purchaseRequest);
        order.setProduct(purchaseRequest.getProduct());
        order.setBuyer(purchaseRequest.getBuyer());
        order.setSeller(purchaseRequest.getProduct().getSeller());
        order.setTotalAmount(purchaseRequest.getProduct().getPrice());
        order.setDepositAmount(purchaseRequest.getDepositAmount());
        order.setPlatformFee(purchaseRequest.getPlatformFee());
        order.setSellerNote(sellerNote);
        order.setStatus(OrderStatus.PENDING_DELIVERY);

        return orderRepository.save(order);
    }

    /**
     * Get order by ID - with ownership check
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Integer orderId, Integer userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        validateOrderAccess(order, userId);
        return OrderResponse.from(order);
    }

    /**
     * Get order by purchase request ID - with ownership check
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderByRequestId(Integer requestId, Integer userId) {
        Order order = orderRepository.findByPurchaseRequest_RequestId(requestId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found for this request"));
        validateOrderAccess(order, userId);
        return OrderResponse.from(order);
    }

    private void validateOrderAccess(Order order, Integer userId) {
        boolean isBuyer = order.getBuyer().getUserId().equals(userId);
        boolean isSeller = order.getSeller().getUserId().equals(userId);
        if (!isBuyer && !isSeller) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to access this order");
        }
    }

    /**
     * Get orders for a buyer
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getBuyerOrders(Integer buyerId, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Order> orders;
        if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            try {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                orders = orderRepository.findByBuyer_UserIdAndStatus(buyerId, orderStatus, pageable);
            } catch (IllegalArgumentException e) {
                orders = orderRepository.findByBuyer_UserId(buyerId, pageable);
            }
        } else {
            orders = orderRepository.findByBuyer_UserId(buyerId, pageable);
        }

        return orders.map(OrderResponse::from);
    }

    /**
     * Get orders for a seller
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getSellerOrders(Integer sellerId, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Order> orders;
        if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            try {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                orders = orderRepository.findBySeller_UserIdAndStatus(sellerId, orderStatus, pageable);
            } catch (IllegalArgumentException e) {
                orders = orderRepository.findBySeller_UserId(sellerId, pageable);
            }
        } else {
            orders = orderRepository.findBySeller_UserId(sellerId, pageable);
        }

        return orders.map(OrderResponse::from);
    }

    /**
     * Update order status (used by delivery flow)
     */
    @Transactional
    public void updateOrderStatus(Integer requestId, OrderStatus newStatus) {
        orderRepository.findByPurchaseRequest_RequestId(requestId).ifPresent(order -> {
            order.setStatus(newStatus);
            orderRepository.save(order);
        });
    }
}
