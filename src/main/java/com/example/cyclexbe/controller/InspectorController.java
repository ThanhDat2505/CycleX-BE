package com.example.cyclexbe.controller;

import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.security.SecurityUtils;
import com.example.cyclexbe.service.InspectorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Inspector Controller - Quản lý duyệt và xử lý listing
 *
 * BP2 — Inspector Module
 * S-20: Dashboard thống kê
 * S-21: Danh sách listing pending/reviewing
 * S-22/S-23: Chi tiết, lock, unlock, approve, reject
 * S-24: Lịch sử review
 * Dispute: Xử lý tranh chấp
 */

@RestController
@RequestMapping("/api/inspector/{inspectorId}")
@PreAuthorize("hasAnyRole('INSPECTOR', 'ADMIN')")
public class InspectorController {

    private final InspectorService inspectorService;

    public InspectorController(InspectorService inspectorService) {
        this.inspectorService = inspectorService;
    }

    /**
     * S-20: Inspector Dashboard
     * Get dashboard statistics with PENDING and DISPUTE counts
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<InspectorDashboardStatsResponse> getDashboardStats(
            @PathVariable Integer inspectorId) {
        SecurityUtils.validateResourceOwner(inspectorId.toString(), "INSPECTOR");
        return ResponseEntity.ok(
                inspectorService.getDashboardStats(inspectorId)
        );
    }

    /**
     * S-21: Pending/Reviewing Listings
     * List listings with filter by status and pagination
     * Query params: status=ALL|PENDING|REVIEWING, sort=newest|oldest, page, page_size
     */
    @GetMapping("/listings")
    public ResponseEntity<Page<SellerListingResponse>> getListings(
            @PathVariable Integer inspectorId,
            @Valid @RequestBody GetInspectorListingsRequest req) {
        SecurityUtils.validateResourceOwner(inspectorId.toString(), "INSPECTOR");
        Page<SellerListingResponse> listings = inspectorService.getListingsForReview(
                req.status, req.sort, req.page, req.pageSize);
        return ResponseEntity.ok(listings);
    }

    /**
     * S-22/S-23: Review Detail
     * Get listing detail for review (includes images if any)
     */
    @GetMapping("/listings/{listingId}/detail")
    public ResponseEntity<PreviewListingResponse> getListingDetail(
            @PathVariable Integer listingId) {
        PreviewListingResponse detail = inspectorService.getListingDetail(listingId);
        return ResponseEntity.ok(detail);
    }

    /**
     * S-22: Lock Listing for Review
     * Lock listing to REVIEWING status and prevent seller from editing
     * POST /inspector/{inspectorId}/listings/{listing_id}/lock
     */
    @PostMapping("/listings/{listing_id}/lock")
    public ResponseEntity<BikeListingResponse> lockListingForReview(
            @PathVariable Integer inspectorId,
            @PathVariable Integer listing_id) {
        System.out.println("Inspector lock listing: " + inspectorId.toString());
        SecurityUtils.validateResourceOwner(inspectorId.toString(), "INSPECTOR");
        BikeListingResponse response = inspectorService.lockListing(listing_id, inspectorId);
        return ResponseEntity.ok(response);
    }

    /**
     * S-22: Unlock Listing from Review
     * Unlock listing and revert status to PENDING (if no decision made yet)
     * POST /inspector/listings/{listing_id}/unlock
     */
    @PostMapping("/listings/{listing_id}/unlock")
    public ResponseEntity<BikeListingResponse> unlockListing(
            @PathVariable Integer inspectorId,
            @PathVariable Integer listing_id) {
        SecurityUtils.validateResourceOwner(inspectorId.toString(), "INSPECTOR");
        BikeListingResponse response = inspectorService.unlockListing(listing_id);
        return ResponseEntity.ok(response);
    }

    /**
     * S-23: Approve Listing
     * Approve listing and change status to APPROVED
     * POST /inspector/{inspectorId}/listings/{listing_id}/approve
     */
    @PostMapping("/listings/{listing_id}/approve")
    public ResponseEntity<BikeListingResponse> approveListing(
            @PathVariable Integer inspectorId,
            @PathVariable Integer listing_id) {
        SecurityUtils.validateResourceOwner(inspectorId.toString(), "INSPECTOR");
        BikeListingResponse response = inspectorService.approveListing(listing_id, inspectorId);
        return ResponseEntity.ok(response);
    }

    /**
     * S-23: Reject Listing
     * Reject listing with reason code, reason text, and optional note
     * POST /inspector/{inspectorId}/listings/{listing_id}/reject
     * Body: {
     *   "reasonCode": "DUPLICATE|INVALID_INFO|LOW_QUALITY|INAPPROPRIATE|OTHER",
     *   "reasonText": "Detailed reason",
     *   "note": "Internal note (optional)"
     * }
     */
    @PostMapping("/listings/reject")
    public ResponseEntity<BikeListingResponse> rejectListing(
            @PathVariable Integer inspectorId,
            @Valid @RequestBody RejectListingRequest req) {
        SecurityUtils.validateResourceOwner(inspectorId.toString(), "INSPECTOR");
        BikeListingResponse response = inspectorService.rejectListing(
                req.listingId, inspectorId, req.reasonCode, req.reasonText, req.note);
        return ResponseEntity.ok(response);
    }

    /**
     * S-24: Review History
     * Get inspector's review history with date range filter
     * Query params: from=YYYY-MM-DD, to=YYYY-MM-DD, page, page_size
     */
    @PostMapping("/reviews")
    public ResponseEntity<Page<BikeListingResponse>> getReviewHistory(
            @PathVariable Integer inspectorId,
            @Valid @RequestBody GetReviewHistoryRequest req) {
        SecurityUtils.validateResourceOwner(inspectorId.toString(), "INSPECTOR");
        Page<BikeListingResponse> reviews = inspectorService.getReviewHistory(
                inspectorId, req.from, req.to, req.page, req.pageSize);
        return ResponseEntity.ok(reviews);
    }

    /**
     * S-24: Review Detail (Optional)
     * Get detail of a specific review
     * GET /inspector/reviews/{review_id}
     */
    @PostMapping("/reviews/detail")
    public ResponseEntity<?> getReviewDetail(
            @PathVariable Integer inspectorId,
            @Valid @RequestBody GetReviewDetailRequest req) {
        SecurityUtils.validateResourceOwner(inspectorId.toString(), "INSPECTOR");
        Object detail = inspectorService.getReviewDetail(req.reviewId);
        return ResponseEntity.ok(detail);
    }

    /**
     * Dispute: List All Disputes
     * Get list of disputes for statistics and routing
     * Query params: status=OPEN|RESOLVED, page, page_size
     */
    @PostMapping("/disputes")
    public ResponseEntity<Page<?>> getDisputes(
            @PathVariable Integer inspectorId,
            @Valid @RequestBody GetDisputesRequest req) {
        SecurityUtils.validateResourceOwner(inspectorId.toString(), "INSPECTOR");
        Page<?> disputes = inspectorService.getDisputes(req.status, req.page, req.pageSize);
        return ResponseEntity.ok(disputes);
    }

    /**
     * Dispute: Dispute Detail
     * Get detail of a specific dispute (if sprint includes deep handling)
     * GET /inspector/disputes/{dispute_id}
     */
    @PostMapping("/disputes/detail")
    public ResponseEntity<?> getDisputeDetail(
            @PathVariable Integer inspectorId,
            @Valid @RequestBody GetDisputeDetailRequest req) {
        SecurityUtils.validateResourceOwner(inspectorId.toString(), "INSPECTOR");
        Object detail = inspectorService.getDisputeDetail(req.disputeId);
        return ResponseEntity.ok(detail);
    }

}
