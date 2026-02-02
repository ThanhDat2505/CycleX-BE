package com.example.cyclexbe.controller;

import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.service.SellerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/seller/{sellerId}")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    // S-10: Seller Dashboard
    @GetMapping("/dashboard/stats")
    public ResponseEntity<SellerDashboardStatsResponse> getDashboardStats(
            @PathVariable Integer sellerId) {
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
        Page<SellerListingResponse> listings = sellerService.getSellerListings(
                sellerId, status, title, brand, model,
                minPrice, maxPrice, sort, page, pageSize);
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/listings/{listingId}/detail")
    public ResponseEntity<SellerListingResponse> getListingDetail(
            @PathVariable Integer sellerId,
            @PathVariable Integer listingId) {
        SellerListingResponse detail = sellerService.getListingDetail(sellerId, listingId);
        return ResponseEntity.ok(detail);
    }

    @PatchMapping("/listings/{listing_id}")
    public ResponseEntity<?> updateListing(
            @PathVariable Integer listing_id,
            @Valid @RequestBody UpdateListingRequest req) {
        // TODO: implement service - edit listing (khi còn Draft/Pending tùy rule)
        return ResponseEntity.ok().build();
    }

    @GetMapping("/listings/{listingId}/rejection")
    public ResponseEntity<SellerListingResponse> getRejectionReason(
            @PathVariable Integer sellerId,
            @PathVariable Integer listingId) {
        SellerListingResponse rejection = sellerService.getRejectionReason(sellerId, listingId);
        return ResponseEntity.ok(rejection);
    }

    // S-12: Create Listing
    @PostMapping("/listings/create")
    public ResponseEntity<BikeListingResponse> createListing(
            @PathVariable Integer sellerId,
            @Valid @RequestBody CreateListingRequest req) {
        BikeListingResponse response = sellerService.createListing(sellerId, req);
        return ResponseEntity.status(201).body(response);
    }

    // S-14: Preview
    @GetMapping("/listings/{listingId}/preview")
    public ResponseEntity<PreviewListingResponse> previewListing(
            @PathVariable Integer sellerId,
            @PathVariable Integer listingId) {
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
        Page<SellerListingResponse> drafts = sellerService.getDraftListings(
                sellerId, sort, page, pageSize);
        return ResponseEntity.ok(drafts);
    }

    @DeleteMapping("/drafts/{listing_id}")
    public ResponseEntity<?> deleteDraft(
            @PathVariable Integer listing_id,
            @PathVariable Integer sellerId) {
        sellerService.deleteDraft(sellerId, listing_id);
        return ResponseEntity.ok(Map.of("message", "Delete success"));
    }

    @PostMapping("/drafts/{listing_id}/submit")
    public ResponseEntity<BikeListingResponse> submitDraft(
            @PathVariable Integer sellerId,
            @PathVariable Integer listing_id) {
        BikeListingResponse response = sellerService.submitListing(sellerId, listing_id);
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
