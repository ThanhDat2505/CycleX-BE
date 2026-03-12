package com.example.cyclexbe.controller;

import com.example.cyclexbe.dto.OrderResponse;
import com.example.cyclexbe.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Get order by ID
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(Authentication authentication, @PathVariable Integer orderId) {
        Integer userId = Integer.parseInt(authentication.getPrincipal().toString());
        return ResponseEntity.ok(orderService.getOrderById(orderId, userId));
    }

    /**
     * Get order by purchase request ID
     */
    @GetMapping("/by-request/{requestId}")
    public ResponseEntity<OrderResponse> getOrderByRequestId(Authentication authentication,
            @PathVariable Integer requestId) {
        Integer userId = Integer.parseInt(authentication.getPrincipal().toString());
        return ResponseEntity.ok(orderService.getOrderByRequestId(requestId, userId));
    }

    /**
     * Get buyer's orders
     */
    @GetMapping("/buyer")
    public ResponseEntity<Page<OrderResponse>> getBuyerOrders(
            Authentication authentication,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Integer buyerId = Integer.parseInt(authentication.getPrincipal().toString());
        return ResponseEntity.ok(orderService.getBuyerOrders(buyerId, status, page, size));
    }

    /**
     * Get seller's orders
     */
    @GetMapping("/seller")
    public ResponseEntity<Page<OrderResponse>> getSellerOrders(
            Authentication authentication,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Integer sellerId = Integer.parseInt(authentication.getPrincipal().toString());
        return ResponseEntity.ok(orderService.getSellerOrders(sellerId, status, page, size));
    }
}
