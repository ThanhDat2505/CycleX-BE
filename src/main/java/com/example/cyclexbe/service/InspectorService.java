package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.BikeListingStatus;
import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.entity.BikeListing;
import com.example.cyclexbe.entity.Product;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.repository.BikeListingRepository;
import com.example.cyclexbe.repository.ProductRepository;
import com.example.cyclexbe.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Service
public class InspectorService {

    private final BikeListingRepository bikeListingRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public InspectorService(BikeListingRepository bikeListingRepository,
                            UserRepository userRepository,
                            ProductRepository productRepository) {
        this.bikeListingRepository = bikeListingRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    /**
     * S-20: Get inspector dashboard statistics
     */
    public InspectorDashboardStatsResponse getDashboardStats(Integer inspectorId) {
        User inspector = userRepository.findById(inspectorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inspector not found"));

        long pendingCount = bikeListingRepository.countByStatus(BikeListingStatus.PENDING);
        long reviewingCount = bikeListingRepository.countByStatus(BikeListingStatus.PENDING); // TODO: Add REVIEWING status
        long approvedCount = bikeListingRepository.countByStatus(BikeListingStatus.APPROVED);
        long rejectedCount = bikeListingRepository.countByStatus(BikeListingStatus.REJECTED);
        long disputeCount = 0; // TODO: Count from disputes table

        return new InspectorDashboardStatsResponse(pendingCount, reviewingCount, approvedCount, rejectedCount, disputeCount);
    }

    /**
     * S-21: Get listings for review
     */
    public Page<SellerListingResponse> getListingsForReview(String status, String sort, int page, int pageSize) {
        Sort.Direction direction = "oldest".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, "createdAt"));

        Page<BikeListing> result;

        if ("PENDING".equalsIgnoreCase(status)) {
            result = bikeListingRepository.findByStatus(BikeListingStatus.PENDING, pageable);
        } else if ("REVIEWING".equalsIgnoreCase(status)) {
            // TODO: Add REVIEWING status to BikeListingStatus enum
            result = bikeListingRepository.findByStatus(BikeListingStatus.PENDING, pageable);
        } else {
            // ALL - get both PENDING and REVIEWING
            result = bikeListingRepository.findByStatus(BikeListingStatus.PENDING, pageable);
        }

        return result.map(SellerListingResponse::from);
    }

    /**
     * S-22/S-23: Get listing detail for review
     */
    public PreviewListingResponse getListingDetail(Integer listingId) {
        BikeListing listing = bikeListingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        return PreviewListingResponse.from(listing);
    }

    /**
     * S-22: Lock listing for review
     */
    public BikeListingResponse lockListing(Integer listingId, Integer inspectorId) {
        BikeListing listing = bikeListingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        if (listing.getStatus() != BikeListingStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PENDING listings can be locked");
        }

        User inspector = userRepository.findById(inspectorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inspector not found"));

        // Lock the listing and assign inspector
        listing.setStatus(BikeListingStatus.REVIEWING);
        listing.setInspector(inspector);
        // TODO: Record lock timestamp

        BikeListing saved = bikeListingRepository.save(listing);
        return BikeListingResponse.from(saved);
    }

    /**
     * S-22: Unlock listing
     */
    public BikeListingResponse unlockListing(Integer listingId) {
        BikeListing listing = bikeListingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        // TODO: Verify status is REVIEWING
        // TODO: Revert to PENDING if no decision made
        if (listing.getStatus() != BikeListingStatus.REVIEWING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only REVIEWING listings can be unlocked");
        }

        listing.setStatus(BikeListingStatus.PENDING);
        listing.setInspector(null);

        BikeListing saved = bikeListingRepository.save(listing);
        return BikeListingResponse.from(saved);
    }

    /**
     * S-23: Approve listing
     */
    public BikeListingResponse approveListing(Integer listingId, Integer inspectorId) {
        BikeListing listing = bikeListingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

<<<<<<< HEAD
        // TODO: Verify status is REVIEWING
        if (listing.getStatus() == BikeListingStatus.APPROVED) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Listing is already approved");
        }

=======
        User inspector = userRepository.findById(inspectorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inspector not found"));

        if (listing.getInspector() != null && !Objects.equals(listing.getInspector().getUserId(), inspectorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the inspector who locked the listing can approve it");
        }

        if (listing.getStatus() != BikeListingStatus.REVIEWING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only REVIEWING listings can be approved");
        }

        // Approve the listing and ensure inspector is set
>>>>>>> origin/InspectorProcess
        listing.setStatus(BikeListingStatus.APPROVED);
        listing.setInspector(inspector);
        // TODO: Record approval decision
        // TODO: Notify seller

        BikeListing savedListing = bikeListingRepository.save(listing);

        // Create Product
        Product product = new Product();
        product.setListing(savedListing);
        product.setSeller(savedListing.getSeller());
        product.setName(savedListing.getTitle());
        product.setDescription(savedListing.getDescription());
        product.setPrice(savedListing.getPrice());
        product.setStatus("AVAILABLE");

        productRepository.save(product);

        return BikeListingResponse.from(savedListing);
    }

    /**
     * S-23: Reject listing
     */
    public BikeListingResponse rejectListing(Integer listingId, Integer inspectorId, String reasonCode, String reasonText, String note) {
        BikeListing listing = bikeListingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        User inspector = userRepository.findById(inspectorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inspector not found"));

        if (listing.getInspector() != null && !Objects.equals(listing.getInspector().getUserId(), inspectorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the inspector who locked the listing can reject it");
        }

        if (listing.getStatus() != BikeListingStatus.REVIEWING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only REVIEWING listings can be rejected");
        }

        // Reject the listing and set inspector
        listing.setStatus(BikeListingStatus.REJECTED);
        listing.setInspector(inspector);
        // TODO: Save rejection reason (code, text, note) to new table
        // TODO: Notify seller with reason

        BikeListing saved = bikeListingRepository.save(listing);
        return BikeListingResponse.from(saved);
    }

    /**
     * S-24: Get review history - lấy tất cả listing mà inspector đã xử lý (APPROVED, REJECTED, REVIEWING)
     */
    public Page<BikeListingResponse> getReviewHistory(Integer inspectorId, String from, String to, int page, int pageSize) {
        User inspector = userRepository.findById(inspectorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inspector not found"));

        // Sắp xếp theo updatedAt (mới nhất trước)
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        // Query listing theo inspector
        Page<BikeListing> listings = bikeListingRepository.findByInspector(inspector, pageable);

        // Filter theo date range nếu có (filter trên content của Page, không trên Page object)
        if ((from != null && !from.isEmpty()) || (to != null && !to.isEmpty())) {
            LocalDateTime fromDate = from != null && !from.isEmpty()
                    ? LocalDate.parse(from).atStartOfDay()
                    : LocalDateTime.MIN;
            LocalDateTime toDate = to != null && !to.isEmpty()
                    ? LocalDate.parse(to).atTime(LocalTime.MAX)
                    : LocalDateTime.now();

            // Tạo list mới sau khi filter
            var filteredContent = listings.getContent().stream()
                    .filter(listing ->
                            (listing.getUpdatedAt().isAfter(fromDate) || listing.getUpdatedAt().isEqual(fromDate)) &&
                            (listing.getUpdatedAt().isBefore(toDate) || listing.getUpdatedAt().isEqual(toDate))
                    )
                    .toList();

            // Convert to response
            return listings.map(BikeListingResponse::from);
        }

        // Convert to response
        return listings.map(BikeListingResponse::from);
    }

    /**
     * S-24: Get review detail
     */
    public Object getReviewDetail(Integer reviewId) {
        // TODO: Query review_decisions table by review_id
        // TODO: Return complete review details

        return null;
    }

    /**
     * Get disputes
     */
    public Page<?> getDisputes(String status, int page, int pageSize) {
        // TODO: Query disputes table
        // TODO: Filter by status (OPEN, RESOLVED)
        // TODO: Return paginated results

        return Page.empty();
    }

    /**
     * Get dispute detail
     */
    public Object getDisputeDetail(Integer disputeId) {
        // TODO: Query disputes table by dispute_id
        // TODO: Include related listing, buyer, seller info

        return null;
    }
}
