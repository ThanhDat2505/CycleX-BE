package com.example.cyclexbe.controller;

import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.security.SecurityUtils;
import com.example.cyclexbe.service.SellerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seller/{sellerId}")
@PreAuthorize("hasRole('SELLER')")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    // S-10: Seller Dashboard
    @GetMapping("/dashboard/stats")
    public ResponseEntity<SellerDashboardStatsResponse> getDashboardStats(
            @PathVariable Integer sellerId) {
        SecurityUtils.validateResourceOwner(sellerId.toString(), "SELLER");
        SellerDashboardStatsResponse stats = sellerService.getDashboardStats(sellerId);
        return ResponseEntity.ok(stats);
    }

    // S-11: My Listings
    @GetMapping("/listings/search")
    public ResponseEntity<Page<SellerListingResponse>> getListings(
            @PathVariable Integer sellerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false, defaultValue = "createdAt") String sort,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        SecurityUtils.validateResourceOwner(sellerId.toString(), "SELLER");
        Page<SellerListingResponse> listings = sellerService.getSellerListings(
                sellerId, status, title, brand, model,
                minPrice, maxPrice, sort, page, pageSize);
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/listings/{listingId}/detail")
    public ResponseEntity<SellerListingResponse> getListingDetail(
            @PathVariable Integer sellerId,
            @PathVariable Integer listingId) {
        SecurityUtils.validateResourceOwner(sellerId.toString(), "SELLER");
        SellerListingResponse detail = sellerService.getListingDetail(sellerId, listingId);
        return ResponseEntity.ok(detail);
    }

    @PatchMapping("/listings/{listing_id}")
    public ResponseEntity<BikeListingResponse> updateListing(
            @PathVariable Integer sellerId,
            @PathVariable Integer listing_id,
            @Valid @RequestBody UpdateListingRequest req) {
        SecurityUtils.validateResourceOwner(sellerId.toString(), "SELLER");
        BikeListingResponse response = sellerService.updateListing(sellerId, listing_id, req);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/listings/{listing_id}/cancel-publish")
    public ResponseEntity<BikeListingResponse> cancelPublish(
            @PathVariable Integer sellerId,
            @PathVariable("listing_id") Integer listingId) {
        SecurityUtils.validateResourceOwner(sellerId.toString(), "SELLER");
        BikeListingResponse response = sellerService.cancelPublishListing(sellerId, listingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/listings/{listingId}/rejection")
    public ResponseEntity<SellerListingResponse> getRejectionReason(
            @PathVariable Integer sellerId,
            @PathVariable Integer listingId) {
        SecurityUtils.validateResourceOwner(sellerId.toString(), "SELLER");
        SellerListingResponse rejection = sellerService.getRejectionReason(sellerId, listingId);
        return ResponseEntity.ok(rejection);
    }

    /**
     * Get listing result: listing info + inspection report (lý do approve/reject)
     * GET /api/seller/{sellerId}/listings/{listingId}/result
     * Response: { listing: SellerListingResponse, inspectionReport: InspectionReportResponse }
     */
    @GetMapping("/listings/{listingId}/result")
    public ResponseEntity<ListingResultResponse> getListingResult(
            @PathVariable Integer sellerId,
            @PathVariable Integer listingId) {
        SecurityUtils.validateResourceOwner(sellerId.toString(), "SELLER");
        ListingResultResponse result = sellerService.getListingResult(sellerId, listingId);
        return ResponseEntity.ok(result);
    }

    // S-12: Create Listing
    @PostMapping("/listings/create")
    public ResponseEntity<BikeListingResponse> createListing(
            @PathVariable Integer sellerId,
            @Valid @RequestBody CreateListingRequest req) {
        SecurityUtils.validateResourceOwner(sellerId.toString(), "SELLER");
        BikeListingResponse response = sellerService.createListing(sellerId, req);
        return ResponseEntity.status(201).body(response);
    }

    // S-14: Preview
    @GetMapping("/listings/{listingId}/preview")
    public ResponseEntity<PreviewListingResponse> previewListing(
            @PathVariable Integer sellerId,
            @PathVariable Integer listingId) {
        SecurityUtils.validateResourceOwner(sellerId.toString(), "SELLER");
        PreviewListingResponse response = sellerService.previewListing(sellerId, listingId);
        return ResponseEntity.ok(response);
    }

    // S-18: Draft Listings
    @GetMapping("/drafts")
    public ResponseEntity<Page<SellerListingResponse>> getDrafts(
            @PathVariable Integer sellerId,
            @RequestParam (required = false, defaultValue = "newest") String sort,
            @RequestParam (required = false, defaultValue = "0") Integer page,
            @RequestParam (required = false, defaultValue = "10") Integer pageSize) {
        SecurityUtils.validateResourceOwner(sellerId.toString(), "SELLER");
        Page<SellerListingResponse> drafts = sellerService.getDraftListings(
                sellerId, sort, page, pageSize);
        return ResponseEntity.ok(drafts);
    }

    @DeleteMapping("/drafts/{listing_id}")
    public ResponseEntity<?> deleteDraft(
            @PathVariable Integer sellerId,
            @PathVariable Integer listing_id) {
        SecurityUtils.validateResourceOwner(sellerId.toString(), "SELLER");
        sellerService.deleteDraft(sellerId, listing_id);
        return ResponseEntity.ok(Map.of("message", "Delete success"));
    }

    @PostMapping("/drafts/{listing_id}/submit")
    public ResponseEntity<BikeListingResponse> submitDraft(
            @PathVariable Integer sellerId,
            @PathVariable Integer listing_id) {
        SecurityUtils.validateResourceOwner(sellerId.toString(), "SELLER");
        BikeListingResponse response = sellerService.submitListing(sellerId, listing_id);
        return ResponseEntity.ok(response);
    }

    // Listing Images (BP1 — S-13)
    @GetMapping("/listings/{listing_id}/images")
    public ResponseEntity<List<ListingImageResponse>> getListingImages(
            @PathVariable Integer sellerId,
            @PathVariable Integer listing_id) {
        SecurityUtils.validateResourceOwner(sellerId.toString(), "SELLER");
        List<ListingImageResponse> images = sellerService.getListingImages(sellerId, listing_id);
        return ResponseEntity.ok(images);
    }

    @PostMapping("/listings/{listing_id}/images")
    public ResponseEntity<ListingImageResponse> uploadListingImage(
            @PathVariable Integer sellerId,
            @PathVariable Integer listing_id,
            @Valid @RequestBody UploadListingImageRequest req) {
        SecurityUtils.validateResourceOwner(sellerId.toString(), "SELLER");
        ListingImageResponse response = sellerService.uploadListingImage(sellerId, listing_id, req);
        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping("/listings/{listing_id}/images/{image_id}")
    public ResponseEntity<?> deleteListingImage(
            @PathVariable Integer sellerId,
            @PathVariable Integer listing_id,
            @PathVariable Integer image_id) {
        SecurityUtils.validateResourceOwner(sellerId.toString(), "SELLER");
        sellerService.deleteListingImage(sellerId, listing_id, image_id);
        return ResponseEntity.ok(Map.of("message", "Image deleted successfully"));
    }

    @PatchMapping("/listings/{listing_id}/images/{image_id}/set-primary")
    public ResponseEntity<?> setImageAsPrimary(
            @PathVariable Integer sellerId,
            @PathVariable Integer listing_id,
            @PathVariable Integer image_id) {
        SecurityUtils.validateResourceOwner(sellerId.toString(), "SELLER");
        sellerService.setImageAsPrimary(sellerId, listing_id, image_id);
        return ResponseEntity.ok(Map.of("message", "Primary image updated"));
    }

    @PostMapping("/listings/{listing_id}/images/{image_id}/retry")
    public ResponseEntity<?> retryImageUpload(
            @PathVariable Integer sellerId,
            @PathVariable Long listing_id,
            @PathVariable Long image_id) {
        SecurityUtils.validateResourceOwner(sellerId.toString(), "SELLER");
        // TODO: implement retry logic if needed
        return ResponseEntity.ok(Map.of("message", "Retry image upload completed"));
    }
}
