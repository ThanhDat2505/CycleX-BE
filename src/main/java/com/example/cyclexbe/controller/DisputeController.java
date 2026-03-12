package com.example.cyclexbe.controller;

import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.service.DisputeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Dispute Controller - Quản lý khiếu nại
 *
 * Buyer endpoints: create dispute, get dispute reasons, check eligibility
 * Inspector/Admin endpoints: list disputes, get detail, resolve
 */
@RestController
public class DisputeController {

    private final DisputeService disputeService;

    public DisputeController(DisputeService disputeService) {
        this.disputeService = disputeService;
    }

    /**
     * Get available dispute reasons
     * GET /api/disputes/reasons
     */
    @GetMapping("/api/disputes/reasons")
    public ResponseEntity<List<DisputeReasonResponse>> getDisputeReasons() {
        return ResponseEntity.ok(disputeService.getDisputeReasons());
    }

    /**
     * Create a new dispute (buyer)
     * POST /api/disputes
     */
    @PostMapping("/api/disputes")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<DisputeDetailResponse> createDispute(@Valid @RequestBody CreateDisputeRequest req) {
        DisputeDetailResponse response = disputeService.createDispute(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Check dispute eligibility for buyer
     * GET /api/disputes/eligibility?buyerId=&orderId=
     */
    @GetMapping("/api/disputes/eligibility")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Map<String, Boolean>> checkEligibility(
            @RequestParam Integer buyerId,
            @RequestParam Integer orderId) {
        boolean allowed = disputeService.checkEligibility(buyerId, orderId);
        return ResponseEntity.ok(Map.of("allowed", allowed));
    }

    /**
     * Check dispute eligibility for buyer (FE compatible path)
     * GET /api/buyers/{buyerId}/dispute-eligibility
     */
    @GetMapping("/api/buyers/{buyerId}/dispute-eligibility")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Map<String, Boolean>> checkBuyerEligibility(
            @PathVariable Integer buyerId,
            @RequestParam(required = false) Integer orderId) {
        boolean allowed = orderId != null && disputeService.checkEligibility(buyerId, orderId);
        return ResponseEntity.ok(Map.of("allowed", allowed));
    }

    /**
     * Get paginated list of disputes (inspector/admin)
     * GET /api/disputes?status=&page=&limit=&q=&sortBy=&sortDir=
     */
    @GetMapping("/api/disputes")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'ADMIN')")
    public ResponseEntity<Page<DisputeListRowResponse>> getDisputes(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortDir,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        Page<DisputeListRowResponse> disputes = disputeService.getDisputes(status, q, sortBy, sortDir, page, limit);
        return ResponseEntity.ok(disputes);
    }

    /**
     * Get dispute detail
     * GET /api/disputes/{disputeId}
     */
    @GetMapping("/api/disputes/{disputeId}")
    @PreAuthorize("hasAnyRole('BUYER', 'SELLER', 'INSPECTOR', 'ADMIN')")
    public ResponseEntity<DisputeDetailResponse> getDisputeDetail(@PathVariable Integer disputeId) {
        DisputeDetailResponse detail = disputeService.getDisputeDetail(disputeId);
        return ResponseEntity.ok(detail);
    }

    /**
     * Resolve a dispute (inspector/admin)
     * POST /api/disputes/{disputeId}/resolve
     */
    @PostMapping("/api/disputes/{disputeId}/resolve")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'ADMIN')")
    public ResponseEntity<DisputeDetailResponse> resolveDispute(
            @PathVariable Integer disputeId,
            @Valid @RequestBody ResolveDisputeRequest req) {
        DisputeDetailResponse response = disputeService.resolveDispute(disputeId, req);
        return ResponseEntity.ok(response);
    }
}
