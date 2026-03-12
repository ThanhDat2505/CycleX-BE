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
                inspectorService.getDashboardStats(inspectorId));
    }

    /**
     * S-21: Pending/Reviewing Listings
     * List listings with filter by status and pagination
     * Query params: status=ALL|PENDING|REVIEWING, sort=newest|oldest, page,
     * page_size
     */
    @GetMapping("/listings")
    public ResponseEntity<Page<SellerListingResponse>> getListings(
            @PathVariable Integer inspectorId,
            @RequestParam(required = false, defaultValue = "ALL") String status,
            @RequestParam(required = false, defaultValue = "newest") String sort,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        SecurityUtils.validateResourceOwner(inspectorId.toString(), "INSPECTOR");
        Page<SellerListingResponse> listings = inspectorService.getListingsForReview(
                inspectorId, status, sort, page, pageSize);
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
        BikeListingResponse response = inspectorService.unlockListing(listing_id, inspectorId);
        return ResponseEntity.ok(response);
    }

    /**
     * S-23: Approve Listing
     * Approve listing and change status to APPROVED. Reason is required.
     * POST /inspector/{inspectorId}/listings/{listing_id}/approve
     * Body: {
     * "reasonText": "Listing meets all standards...",
     * "reasonCode": "MEETS_STANDARDS|GOOD_CONDITION|OTHER" (optional),
     * "note": "Internal note (optional)"
     * }
     */
    @PostMapping("/listings/{listing_id}/approve")
    public ResponseEntity<BikeListingResponse> approveListing(
            @PathVariable Integer inspectorId,
            @PathVariable Integer listing_id,
            @Valid @RequestBody ApproveListingRequest req) {
        SecurityUtils.validateResourceOwner(inspectorId.toString(), "INSPECTOR");
        BikeListingResponse response = inspectorService.approveListing(
                listing_id, inspectorId, req.reasonCode, req.reasonText, req.note);
        return ResponseEntity.ok(response);
    }

    /**
     * S-23: Reject Listing
     * Reject listing with reason code, reason text, and optional note
     * POST /inspector/{inspectorId}/listings/{listing_id}/reject
     * Body: {
     * "reasonCode": "DUPLICATE|INVALID_INFO|LOW_QUALITY|INAPPROPRIATE|OTHER",
     * "reasonText": "Detailed reason",
     * "note": "Internal note (optional)"
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
     * Query params: from=YYYY-MM-DD, to=YYYY-MM-DD, page, pageSize
     */
    @GetMapping("/reviews")
    public ResponseEntity<Page<BikeListingResponse>> getReviewHistory(
            @PathVariable Integer inspectorId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        SecurityUtils.validateResourceOwner(inspectorId.toString(), "INSPECTOR");
        Page<BikeListingResponse> reviews = inspectorService.getReviewHistory(
                inspectorId, from, to, page, pageSize);
        return ResponseEntity.ok(reviews);
    }

    /**
     * S-24: Review Detail - Get inspection report for a listing
     * GET /api/inspector/{inspectorId}/reviews/{listingId}
     */
    @GetMapping("/reviews/{listingId}")
    public ResponseEntity<InspectionReportResponse> getReviewDetail(
            @PathVariable Integer inspectorId,
            @PathVariable Integer listingId) {
        SecurityUtils.validateResourceOwner(inspectorId.toString(), "INSPECTOR");
        InspectionReportResponse detail = inspectorService.getReviewDetail(listingId);
        return ResponseEntity.ok(detail);
    }

    /**
     * Get Inspection Report for a listing
     * GET /api/inspector/{inspectorId}/listings/{listingId}/report
     * Returns the latest InspectionReport (approval/rejection reason) for the
     * listing
     */
    @GetMapping("/listings/{listingId}/report")
    public ResponseEntity<InspectionReportResponse> getInspectionReport(
            @PathVariable Integer inspectorId,
            @PathVariable Integer listingId) {
        SecurityUtils.validateResourceOwner(inspectorId.toString(), "INSPECTOR");
        InspectionReportResponse report = inspectorService.getInspectionReportByListing(listingId);
        return ResponseEntity.ok(report);
    }

    /**
     * Dispute: List All Disputes
     * Query params: status=OPEN|RESOLVED, page, pageSize
     */
    @GetMapping("/disputes")
    public ResponseEntity<Page<DisputeListRowResponse>> getDisputes(
            @PathVariable Integer inspectorId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        SecurityUtils.validateResourceOwner(inspectorId.toString(), "INSPECTOR");
        Page<DisputeListRowResponse> disputes = inspectorService.getDisputes(status, page, pageSize);
        return ResponseEntity.ok(disputes);
    }

    /**
     * Dispute: Dispute Detail
     * GET /api/inspector/{inspectorId}/disputes/{disputeId}
     */
    @GetMapping("/disputes/{disputeId}")
    public ResponseEntity<DisputeDetailResponse> getDisputeDetail(
            @PathVariable Integer inspectorId,
            @PathVariable Integer disputeId) {
        SecurityUtils.validateResourceOwner(inspectorId.toString(), "INSPECTOR");
        DisputeDetailResponse detail = inspectorService.getDisputeDetail(disputeId);
        return ResponseEntity.ok(detail);
    }

}
