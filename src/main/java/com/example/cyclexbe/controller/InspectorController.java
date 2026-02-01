package com.example.cyclexbe.controller;

import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.service.InspectorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/inspector")
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
            @Valid @RequestBody GetDashboardStatsRequest req) {
        System.out.println("inspectorId = " + req.inspectorId);
        return ResponseEntity.ok(
                inspectorService.getDashboardStats(req.inspectorId)
        );
    }

    /**
     * S-21: Pending/Reviewing Listings
     * List listings with filter by status and pagination
     * Query params: status=ALL|PENDING|REVIEWING, sort=newest|oldest, page, page_size
     */
    @PostMapping("/listings")
    public ResponseEntity<Page<SellerListingResponse>> getListings(
            @Valid @RequestBody GetInspectorListingsRequest req) {
        Page<SellerListingResponse> listings = inspectorService.getListingsForReview(
                req.status, req.sort, req.page, req.pageSize);
        return ResponseEntity.ok(listings);
    }

    /**
     * S-22/S-23: Review Detail
     * Get listing detail for review (includes images if any)
     */
    @PostMapping("/listings/detail")
    public ResponseEntity<PreviewListingResponse> getListingDetail(
            @Valid @RequestBody GetInspectorListingDetailRequest req) {
        PreviewListingResponse detail = inspectorService.getListingDetail(req.listingId);
        return ResponseEntity.ok(detail);
    }

    /**
     * S-22: Lock Listing for Review
     * Lock listing to REVIEWING status and prevent seller from editing
     * POST /inspector/listings/{listing_id}/lock
     */
    @PostMapping("/listings/{listing_id}/lock")
    public ResponseEntity<BikeListingResponse> lockListingForReview(
            @Valid @RequestBody LockListingRequest req,
            @PathVariable Integer listing_id) {
        BikeListingResponse response = inspectorService.lockListing(listing_id);
        return ResponseEntity.ok(response);
    }

    /**
     * S-22: Unlock Listing from Review
     * Unlock listing and revert status to PENDING (if no decision made yet)
     * POST /inspector/listings/{listing_id}/unlock
     */
    @PostMapping("/listings/{listing_id}/unlock")
    public ResponseEntity<BikeListingResponse> unlockListing(
            @Valid @RequestBody UnlockListingRequest req,
            @PathVariable Integer listing_id) {
        BikeListingResponse response = inspectorService.unlockListing(listing_id);
        return ResponseEntity.ok(response);
    }

    /**
     * S-23: Approve Listing
     * Approve listing and change status to APPROVED
     * POST /inspector/listings/{listing_id}/approve
     */
    @PostMapping("/listings/{listing_id}/approve")
    public ResponseEntity<BikeListingResponse> approveListing(
            @Valid @RequestBody ApproveListingRequest req,
            @PathVariable Integer listing_id) {
        BikeListingResponse response = inspectorService.approveListing(listing_id);
        return ResponseEntity.ok(response);
    }

    /**
     * S-23: Reject Listing
     * Reject listing with reason code, reason text, and optional note
     * POST /inspector/listings/{listing_id}/reject
     * Body: {
     *   "reasonCode": "DUPLICATE|INVALID_INFO|LOW_QUALITY|INAPPROPRIATE|OTHER",
     *   "reasonText": "Detailed reason",
     *   "note": "Internal note (optional)"
     * }
     */
    @PostMapping("/listings/{listing_id}/reject")
    public ResponseEntity<BikeListingResponse> rejectListing(
            @Valid @RequestBody RejectListingRequest req,
            @PathVariable Integer listing_id) {
        BikeListingResponse response = inspectorService.rejectListing(
                req.listingId, req.reasonCode, req.reasonText, req.note);
        return ResponseEntity.ok(response);
    }

    /**
     * S-24: Review History
     * Get inspector's review history with date range filter
     * Query params: from=YYYY-MM-DD, to=YYYY-MM-DD, page, page_size
     */
    @PostMapping("/reviews")
    public ResponseEntity<Page<?>> getReviewHistory(
            @Valid @RequestBody GetReviewHistoryRequest req) {
        Page<?> reviews = inspectorService.getReviewHistory(
                req.inspectorId, req.from, req.to, req.page, req.pageSize);
        return ResponseEntity.ok(reviews);
    }

    /**
     * S-24: Review Detail (Optional)
     * Get detail of a specific review
     * GET /inspector/reviews/{review_id}
     */
    @PostMapping("/reviews/detail")
    public ResponseEntity<?> getReviewDetail(
            @Valid @RequestBody GetReviewDetailRequest req) {
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
            @Valid @RequestBody GetDisputesRequest req) {
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
            @Valid @RequestBody GetDisputeDetailRequest req) {
        Object detail = inspectorService.getDisputeDetail(req.disputeId);
        return ResponseEntity.ok(detail);
    }

}
