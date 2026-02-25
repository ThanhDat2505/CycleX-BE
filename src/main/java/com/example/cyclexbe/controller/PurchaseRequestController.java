package com.example.cyclexbe.controller;

import com.example.cyclexbe.dto.PurchaseRequestCreateRequest;
import com.example.cyclexbe.dto.PurchaseRequestInitResponse;
import com.example.cyclexbe.dto.PurchaseRequestResponse;
import com.example.cyclexbe.dto.PurchaseRequestReviewResponse;
import com.example.cyclexbe.service.PurchaseRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for S-50: Purchase Request feature
 * Handles buyer's purchase request creation flow
 */
@RestController
@RequestMapping("/api/listings")
public class PurchaseRequestController {

    private final PurchaseRequestService purchaseRequestService;

    public PurchaseRequestController(PurchaseRequestService purchaseRequestService) {
        this.purchaseRequestService = purchaseRequestService;
    }

    /**
     * GET /api/listings/{listingId}/purchase-request/init
     * Initialize purchase request screen with listing & pricing info
     */
    @GetMapping("/{listingId}/purchase-request/init")
    public ResponseEntity<PurchaseRequestInitResponse> initPurchaseRequest(
            @PathVariable Integer listingId) {
        Integer buyerId = extractBuyerIdFromAuth();
        PurchaseRequestInitResponse response = purchaseRequestService.getInitData(listingId, buyerId);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/listings/{listingId}/purchase-requests/review
     * Review & validate purchase request before confirmation
     * Does NOT create DB record
     */
    @PostMapping("/{listingId}/purchase-requests/review")
    public ResponseEntity<PurchaseRequestReviewResponse> reviewPurchaseRequest(
            @PathVariable Integer listingId,
            @Valid @RequestBody PurchaseRequestCreateRequest request) {
        Integer buyerId = extractBuyerIdFromAuth();
        PurchaseRequestReviewResponse response = purchaseRequestService.reviewPurchaseRequest(listingId, buyerId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/listings/{listingId}/purchase-requests
     * Create purchase request with status = PENDING_SELLER_CONFIRM
     */
    @PostMapping("/{listingId}/purchase-requests")
    public ResponseEntity<PurchaseRequestResponse> createPurchaseRequest(
            @PathVariable Integer listingId,
            @Valid @RequestBody PurchaseRequestCreateRequest request) {
        Integer buyerId = extractBuyerIdFromAuth();
        PurchaseRequestResponse response = purchaseRequestService.createPurchaseRequest(listingId, buyerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Extract buyer ID from Spring Security Authentication
     * TODO: Handle case when authentication is not available (mock or error)
     */
    private Integer extractBuyerIdFromAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal == null || "anonymousUser".equals(principal)) {
            throw new RuntimeException("User is not authenticated");
        }

        // Case 1: principal là String chứa userId (tạm thời)
        if (principal instanceof String principalStr) {
            try {
                return Integer.parseInt(principalStr);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid user ID in authentication: " + principalStr);
            }
        }

        // Case 2: TODO - custom principal/UserDetails
        // Ví dụ:
        // if (principal instanceof CustomUserPrincipal custom) {
        //     return custom.getUserId();
        // }

        throw new RuntimeException("Unsupported authentication principal type: " + principal.getClass().getName());
    }
}

