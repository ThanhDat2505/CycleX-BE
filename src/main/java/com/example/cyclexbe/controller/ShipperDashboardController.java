package com.example.cyclexbe.controller;

import com.example.cyclexbe.dto.ShipperAssignedDeliveryListResponse;
import com.example.cyclexbe.dto.ShipperDashboardSummaryResponse;
import com.example.cyclexbe.service.ShipperDashboardService;
import com.example.cyclexbe.service.ShipperDeliveryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for S-60: Shipper Dashboard – BP6
 * Handles shipper dashboard summary and assigned deliveries list
 */
@RestController
@RequestMapping("/api/shipper")
public class ShipperDashboardController {

    private final ShipperDashboardService shipperDashboardService;
    private final ShipperDeliveryService shipperDeliveryService;

    public ShipperDashboardController(
            ShipperDashboardService shipperDashboardService,
            ShipperDeliveryService shipperDeliveryService) {
        this.shipperDashboardService = shipperDashboardService;
        this.shipperDeliveryService = shipperDeliveryService;
    }

    /**
     * F1: GET /api/shipper/dashboard/summary
     * Get dashboard summary for the current shipper
     * Returns counts of assigned, in-progress, and failed deliveries
     *
     * @return Dashboard summary response with counts and timestamp
     */
    @GetMapping("/dashboard/summary")
    public ResponseEntity<ShipperDashboardSummaryResponse> getDashboardSummary() {
        Integer shipperId = extractShipperIdFromAuth();
        ShipperDashboardSummaryResponse response = shipperDashboardService.getSummary(shipperId);
        return ResponseEntity.ok(response);
    }

    /**
     * F2: GET /api/shipper/deliveries/assigned
     * Get assigned deliveries for the current shipper with pagination
     * Returns only deliveries with status = ASSIGNED
     *
     * @param page Zero-based page number (default 0)
     * @param pageSize Page size (default 10)
     * @return Paginated list of assigned deliveries
     */
    @GetMapping("/deliveries/assigned")
    public ResponseEntity<ShipperAssignedDeliveryListResponse> getAssignedDeliveries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Integer shipperId = extractShipperIdFromAuth();
        Pageable pageable = PageRequest.of(page, pageSize);
        ShipperAssignedDeliveryListResponse response = shipperDeliveryService.getAssignedDeliveries(shipperId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Extract shipper ID from Spring Security Authentication
     * Follows the same pattern as BuyerTransactionController
     *
     * @return Shipper ID (user ID)
     * @throws RuntimeException if authentication is not available
     */
    private Integer extractShipperIdFromAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal == null || "anonymousUser".equals(principal)) {
            throw new RuntimeException("User is not authenticated");
        }

        // Case 1: principal is String containing userId (temporary)
        if (principal instanceof String principalStr) {
            try {
                return Integer.parseInt(principalStr);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid user ID in authentication: " + principalStr);
            }
        }

        // Case 2: TODO - custom principal/UserDetails
        // Example:
        // if (principal instanceof CustomUserPrincipal custom) {
        //     return custom.getUserId();
        // }

        throw new RuntimeException("Unsupported authentication principal type: " + principal.getClass().getName());
    }
}

