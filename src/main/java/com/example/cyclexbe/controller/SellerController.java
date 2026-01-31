package com.example.cyclexbe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/seller")
public class SellerController {

    // S-10: Seller Dashboard
    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getDashboardStats() {
        // TODO: implement service - counts ACTIVE/PENDING/REJECTED + số giao dịch
        return ResponseEntity.ok().build();
    }

    // S-11: My Listings
    @GetMapping("/listings")
    public ResponseEntity<?> getListings(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int page_size) {
        // TODO: implement service - list listing của seller
        // status: Draft|Pending|Active|Rejected
        // sort: newest|oldest
        return ResponseEntity.ok().build();
    }

    @GetMapping("/listings/{listing_id}")
    public ResponseEntity<?> getListingDetail(@PathVariable Long listing_id) {
        // TODO: implement service - seller view detail (status duyệt/inspection/transaction nếu có)
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/listings/{listing_id}")
    public ResponseEntity<?> updateListing(
            @PathVariable Long listing_id,
            @RequestBody Object listingData) {
        // TODO: implement service - edit listing (khi còn Draft/Pending tùy rule)
        return ResponseEntity.ok().build();
    }

    @GetMapping("/listings/{listing_id}/rejection")
    public ResponseEntity<?> getRejectionReason(@PathVariable Long listing_id) {
        // TODO: implement service - lý do reject + note (nếu REJECTED)
        return ResponseEntity.ok().build();
    }

    // S-12: Create Listing (Form) + Draft
    @PostMapping("/listings")
    public ResponseEntity<?> createListing(@RequestBody Object listingData) {
        // TODO: implement service - tạo listing (mặc định Draft hoặc theo payload save_as=draft)
        return ResponseEntity.ok().build();
    }

    @PostMapping("/listings/{listing_id}/submit")
    public ResponseEntity<?> submitListing(@PathVariable Long listing_id) {
        // TODO: implement service - submit listing → PENDING_APPROVAL (validate server-side)
        return ResponseEntity.ok().build();
    }

    // S-14: Preview
    @GetMapping("/listings/{listing_id}/preview")
    public ResponseEntity<?> previewListing(@PathVariable Long listing_id) {
        // TODO: implement service - dữ liệu preview (compose/normalize để FE render)
        return ResponseEntity.ok().build();
    }

    // S-18: Draft Listings
    @GetMapping("/drafts")
    public ResponseEntity<?> getDrafts(
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int page_size) {
        // TODO: implement service - list draft
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/drafts/{listing_id}")
    public ResponseEntity<?> deleteDraft(@PathVariable Long listing_id) {
        // TODO: implement service - delete draft
        return ResponseEntity.ok().build();
    }

    @PostMapping("/drafts/{listing_id}/submit")
    public ResponseEntity<?> submitDraft(@PathVariable Long listing_id) {
        // TODO: implement service - submit draft → PENDING_APPROVAL
        return ResponseEntity.ok().build();
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
