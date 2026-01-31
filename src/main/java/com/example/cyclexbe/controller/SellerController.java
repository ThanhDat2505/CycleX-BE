package com.example.cyclexbe.controller;

import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.service.SellerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/seller")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    // S-10: Seller Dashboard
    @GetMapping("/dashboard/stats")
    public ResponseEntity<SellerDashboardStatsResponse> getDashboardStats(
            @Valid @RequestBody GetDashboardStatsRequest req) {
        SellerDashboardStatsResponse stats = sellerService.getDashboardStats(req.sellerId);
        return ResponseEntity.ok(stats);
    }

    // S-11: My Listings
    @PostMapping("/listings/search")
    public ResponseEntity<Page<SellerListingResponse>> getListings(
            @Valid @RequestBody GetListingsRequest req) {
        Page<SellerListingResponse> listings = sellerService.getSellerListings(
                req.sellerId, req.status, req.title, req.brand, req.model,
                req.minPrice, req.maxPrice, req.sort, req.page, req.pageSize);
        return ResponseEntity.ok(listings);
    }

    @PostMapping("/listings/detail")
    public ResponseEntity<SellerListingResponse> getListingDetail(
            @Valid @RequestBody GetListingDetailRequest req) {
        SellerListingResponse detail = sellerService.getListingDetail(req.sellerId, req.listingId);
        return ResponseEntity.ok(detail);
    }

    @PatchMapping("/listings/{listing_id}")
    public ResponseEntity<?> updateListing(
            @Valid @RequestBody UpdateListingRequest req,
            @PathVariable Integer listing_id) {
        // TODO: implement service - edit listing (khi còn Draft/Pending tùy rule)
        return ResponseEntity.ok().build();
    }

    @PostMapping("/listings/rejection")
    public ResponseEntity<SellerListingResponse> getRejectionReason(
            @Valid @RequestBody GetListingDetailRequest req) {
        SellerListingResponse rejection = sellerService.getRejectionReason(req.sellerId, req.listingId);
        return ResponseEntity.ok(rejection);
    }

    // S-12: Create Listing
    @PostMapping("/listings")
    public ResponseEntity<BikeListingResponse> createListing(
            @Valid @RequestBody CreateListingRequest req) {
        BikeListingResponse response = sellerService.createListing(req.sellerId, req);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/listings/{listing_id}/submit")
    public ResponseEntity<BikeListingResponse> submitListing(
            @Valid @RequestBody SubmitListingRequest req,
            @PathVariable Integer listing_id) {
        BikeListingResponse response = sellerService.submitListing(req.sellerId, req.listingId);
        return ResponseEntity.ok(response);
    }

    // S-14: Preview
    @PostMapping("/listings/preview")
    public ResponseEntity<PreviewListingResponse> previewListing(
            @Valid @RequestBody PreviewListingRequest req) {
        PreviewListingResponse response = sellerService.previewListing(req.sellerId, req.listingId);
        return ResponseEntity.ok(response);
    }

    // S-18: Draft Listings
    @PostMapping("/drafts")
    public ResponseEntity<Page<SellerListingResponse>> getDrafts(
            @Valid @RequestBody GetDraftsRequest req) {
        Page<SellerListingResponse> drafts = sellerService.getDraftListings(
                req.sellerId, req.sort, req.page, req.pageSize);
        return ResponseEntity.ok(drafts);
    }

    @DeleteMapping("/drafts/{listing_id}")
    public ResponseEntity<?> deleteDraft(
            @Valid @RequestBody DeleteDraftRequest req,
            @PathVariable Integer listing_id) {
        sellerService.deleteDraft(req.sellerId, req.listingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/drafts/{listing_id}/submit")
    public ResponseEntity<BikeListingResponse> submitDraft(
            @Valid @RequestBody SubmitListingRequest req,
            @PathVariable Integer listing_id) {
        BikeListingResponse response = sellerService.submitListing(req.sellerId, req.listingId);
        return ResponseEntity.ok(response);
    }

    // Listing Images (BP1 — S-13)
    @GetMapping("/listings/{listing_id}/images")
    public ResponseEntity<?> getListingImages(@PathVariable Long listing_id) {
        // TODO: implement service - list ảnh hiện có
        return ResponseEntity.ok().build();
    }

    @PostMapping("/listings/{listing_id}/images")
    public ResponseEntity<?> uploadListingImage(
            @PathVariable Long listing_id,
            @RequestParam("file") MultipartFile file) {
        // TODO: implement service - upload ảnh (multipart)
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/listings/{listing_id}/images/{image_id}")
    public ResponseEntity<?> deleteListingImage(
            @PathVariable Long listing_id,
            @PathVariable Long image_id) {
        // TODO: implement service - xóa ảnh
        return ResponseEntity.ok().build();
    }

    @PostMapping("/listings/{listing_id}/images/{image_id}/retry")
    public ResponseEntity<?> retryImageUpload(
            @PathVariable Long listing_id,
            @PathVariable Long image_id) {
        // TODO: implement service - retry upload (nếu lưu state lỗi)
        return ResponseEntity.ok().build();
    }
}
