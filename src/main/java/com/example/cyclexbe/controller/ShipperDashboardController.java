package com.example.cyclexbe.controller;

import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.service.ShipperDashboardService;
import com.example.cyclexbe.service.ShipperDeliveryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for S-60: Shipper Dashboard – BP6
 * Handles shipper dashboard summary and assigned deliveries list
 * Also handles S-61: Shipper Delivery Details (F1-F8)
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
     * S-61 F1/F2: GET /api/shipper/deliveries?status=...&page=...&size=...&sort=...
     * Load and filter list of assigned deliveries with flexible status filtering
     * Auth: SHIPPER (extracted from JWT, no path parameter needed)
     *
     * Query Params:
     * - status (optional): Filter by status (ASSIGNED, IN_PROGRESS, FAILED)
     *   Can be single value or comma-separated for multiple statuses
     * - page (optional, default 0): Zero-based page number
     * - size (optional, default 10): Page size
     * - sort (optional, default createdAt desc): Sort field and direction
     *
     * Response:
     * {
     *   "page": 0,
     *   "size": 10,
     *   "totalElements": 25,
     *   "totalPages": 3,
     *   "items": [
     *     {
     *       "deliveryId": 101,
     *       "orderId": 9001,
     *       "pickupCity": "Ho Chi Minh",
     *       "deliveryCity": "Da Nang",
     *       "status": "ASSIGNED",
     *       "scheduledTime": "2026-03-05T10:30:00"
     *     }
     *   ]
     * }
     *
     * Error:
     * - 400 if status is invalid (not in ASSIGNED, IN_PROGRESS, FAILED)
     * - 401/403 if not authenticated or not SHIPPER role
     *
     * @param status Optional status filter (comma-separated allowed)
     * @param page Zero-based page number
     * @param size Page size
     * @param sort Sort field with direction (format: "field,asc" or "field,desc")
     * @return Paginated list of deliveries with filters applied
     */
    @GetMapping("/deliveries")
    public ResponseEntity<ShipperDeliveryListResponse> getDeliveries(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        Integer shipperId = extractShipperIdFromAuth();

        // Parse sort parameter (format: "field,direction")
        String[] sortParts = sort.split(",");
        String sortField = sortParts.length > 0 ? sortParts[0] : "createdAt";
        String direction = sortParts.length > 1 ? sortParts[1].toLowerCase() : "desc";
        Sort.Direction sortDirection = "asc".equals(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        ShipperDeliveryListResponse response = shipperDeliveryService.getDeliveriesByStatus(
                shipperId, status, pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * S-61 F3/F4/F7/F8: GET /api/shipper/deliveries/{deliveryId}
     * Get delivery detail with full information, timeline (readonly), and available actions
     * Auth: SHIPPER (extracted from JWT)
     * Security (F7): Returns 403 if delivery doesn't belong to current shipper
     *
     * Response:
     * {
     *   "deliveryId": 101,
     *   "orderId": 9001,
     *   "status": "IN_PROGRESS",
     *   "seller": { "userId": 11, "fullName": "Nguyen Van A", "phone": "0909xxxx" },
     *   "buyer": { "userId": 22, "fullName": "Tran Thi B", "phone": "0912xxxx" },
     *   "pickup": {
     *     "address": "12 Nguyen Trai, Q1",
     *     "city": "Ho Chi Minh",
     *     "contactName": "Nguyen Van A",
     *     "contactPhone": "0909xxxx"
     *   },
     *   "delivery": {
     *     "address": "99 Le Duan, Hai Chau",
     *     "city": "Da Nang",
     *     "contactName": "Tran Thi B",
     *     "contactPhone": "0912xxxx"
     *   },
     *   "timeline": {
     *     "assignedTime": "2026-03-05T08:10:00",
     *     "shippedTime": "2026-03-05T09:00:00",
     *     "expectedDeliveryTime": "2026-03-06T18:00:00",
     *     "completedTime": null
     *   },
     *   "actions": {
     *     "canConfirm": true,
     *     "canReportFailed": false,
     *     "message": null
     *   }
     * }
     *
     * Note on F5/F6 (navigate):
     * - No separate API needed. FE uses status or actions.canConfirm/canReportFailed to enable buttons
     * - IN_PROGRESS status enables S-63 (Confirm Delivery)
     * - FAILED status enables S-64 (Report Failed)
     *
     * Note on F8 (refresh):
     * - FE can call this endpoint again to get updated status
     * - If status becomes COMPLETED or out of valid states, actions.message will indicate no actions available
     *
     * Error:
     * - 403 if delivery belongs to different shipper
     * - 404 if delivery not found
     * - 401/403 if not authenticated or not SHIPPER role
     *
     * @param deliveryId Delivery ID from path parameter
     * @return Full delivery detail response with timeline and actions
     */
    @GetMapping("/deliveries/{deliveryId}")
    public ResponseEntity<ShipperDeliveryDetailResponse> getDeliveryDetail(
            @PathVariable Integer deliveryId) {

        Integer shipperId = extractShipperIdFromAuth();
        ShipperDeliveryDetailResponse response = shipperDeliveryService.getDeliveryDetail(deliveryId, shipperId);

        return ResponseEntity.ok(response);
    }

    /**
     * Extract shipper ID from Spring Security Authentication
     * Follows the same pattern as BuyerTransactionController
     * Shipper ID is taken from JWT token, not from path parameter
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

