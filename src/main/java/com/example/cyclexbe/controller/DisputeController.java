package com.example.cyclexbe.controller;

import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.security.SecurityUtils;
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
 * Inspector endpoints: list disputes, get detail, resolve, escalate
 * Admin endpoints: list all disputes, override, final decision
 */
@RestController
public class DisputeController {

    private final DisputeService disputeService;

    public DisputeController(DisputeService disputeService) {
        this.disputeService = disputeService;
    }

    // ==================== BUYER ENDPOINTS ====================

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

    // ==================== INSPECTOR / ADMIN SHARED ENDPOINTS ====================

    /**
     * Get paginated list of all disputes (inspector/admin)
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
     * Actions: REFUND_BUYER (buyer wins), RELEASE_FUND_SELLER (seller wins), CLOSE_CASE (reject)
     */
    @PostMapping("/api/disputes/{disputeId}/resolve")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'ADMIN')")
    public ResponseEntity<DisputeDetailResponse> resolveDispute(
            @PathVariable Integer disputeId,
            @Valid @RequestBody ResolveDisputeRequest req) {
        DisputeDetailResponse response = disputeService.resolveDispute(disputeId, req);
        return ResponseEntity.ok(response);
    }

    // ==================== INSPECTOR-SPECIFIC ENDPOINTS ====================

    /**
     * Get disputes assigned to a specific inspector
     * GET /api/inspector/{userId}/disputes
     */
    @GetMapping("/api/inspector/{userId}/disputes")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'ADMIN')")
    public ResponseEntity<Page<DisputeListRowResponse>> getInspectorDisputes(
            @PathVariable Integer userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortDir,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        Page<DisputeListRowResponse> disputes = disputeService.getDisputesByAssignee(userId, status, q, sortBy, sortDir, page, limit);
        return ResponseEntity.ok(disputes);
    }

    /**
     * Get dispute detail for inspector
     * GET /api/inspector/{userId}/disputes/{disputeId}
     */
    @GetMapping("/api/inspector/{userId}/disputes/{disputeId}")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'ADMIN')")
    public ResponseEntity<DisputeDetailResponse> getInspectorDisputeDetail(
            @PathVariable Integer userId,
            @PathVariable Integer disputeId) {
        return ResponseEntity.ok(disputeService.getDisputeDetail(disputeId));
    }

    /**
     * Claim/accept a dispute (inspector picks it up → IN_PROGRESS)
     * POST /api/disputes/{disputeId}/claim
     */
    @PostMapping("/api/disputes/{disputeId}/claim")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'ADMIN')")
    public ResponseEntity<DisputeDetailResponse> claimDispute(@PathVariable Integer disputeId) {
        String authUserId = SecurityUtils.getAuthenticatedUserId();
        DisputeDetailResponse response = disputeService.claimDispute(disputeId, Integer.parseInt(authUserId));
        return ResponseEntity.ok(response);
    }

    /**
     * Escalate a dispute to Admin (inspector cannot resolve)
     * POST /api/disputes/{disputeId}/escalate
     */
    @PostMapping("/api/disputes/{disputeId}/escalate")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'ADMIN')")
    public ResponseEntity<DisputeDetailResponse> escalateDispute(
            @PathVariable Integer disputeId,
            @RequestBody(required = false) Map<String, String> body) {
        String note = body != null ? body.get("note") : null;
        DisputeDetailResponse response = disputeService.escalateDispute(disputeId, note);
        return ResponseEntity.ok(response);
    }

    // ==================== ADMIN-SPECIFIC ENDPOINTS ====================

    /**
     * Get all disputes for admin view
     * GET /api/admin/disputes
     */
    @GetMapping("/api/admin/disputes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<DisputeListRowResponse>> getAdminDisputes(
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
     * Get dispute detail for admin
     * GET /api/admin/disputes/{disputeId}
     */
    @GetMapping("/api/admin/disputes/{disputeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DisputeDetailResponse> getAdminDisputeDetail(@PathVariable Integer disputeId) {
        return ResponseEntity.ok(disputeService.getDisputeDetail(disputeId));
    }

    /**
     * Admin list disputes (alternate endpoint for FE compatibility)
     * GET /api/admin/disputes/list
     */
    @GetMapping("/api/admin/disputes/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<DisputeListRowResponse>> getAdminDisputesList(
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
     * Admin override / final decision (S-83)
     * POST /api/admin/disputes/{disputeId}/override
     * Actions: BUYER_WIN, SELLER_WIN, SPLIT
     */
    @PostMapping("/api/admin/disputes/{disputeId}/override")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> adminOverride(
            @PathVariable Integer disputeId,
            @Valid @RequestBody AdminOverrideRequest req) {
        disputeService.adminOverride(disputeId, req);
        return ResponseEntity.ok().build();
    }
}
