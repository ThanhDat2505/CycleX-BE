package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.BikeListingStatus;
import com.example.cyclexbe.domain.enums.DisputeStatus;
import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.entity.BikeListing;
import com.example.cyclexbe.entity.InspectionReport;
import com.example.cyclexbe.entity.Product;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.repository.BikeListingRepository;
import com.example.cyclexbe.repository.DisputeRepository;
import com.example.cyclexbe.repository.InspectionReportRepository;
import com.example.cyclexbe.repository.ListingImageRepository;
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
import java.util.List;
import java.util.Objects;

@Service
public class InspectorService {

    private final BikeListingRepository bikeListingRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InspectionReportRepository inspectionReportRepository;
    private final ListingImageRepository listingImageRepository;
    private final DisputeRepository disputeRepository;

    public InspectorService(BikeListingRepository bikeListingRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            InspectionReportRepository inspectionReportRepository,
            ListingImageRepository listingImageRepository,
            DisputeRepository disputeRepository) {
        this.bikeListingRepository = bikeListingRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.inspectionReportRepository = inspectionReportRepository;
        this.listingImageRepository = listingImageRepository;
        this.disputeRepository = disputeRepository;
    }

    /**
     * S-20: Get inspector dashboard statistics
     * Only count listings assigned to this inspector
     */
    public InspectorDashboardStatsResponse getDashboardStats(Integer inspectorId) {
        User inspector = userRepository.findById(inspectorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy kiểm duyệt viên"));

        long pendingCount = bikeListingRepository.countByInspectorAndStatus(inspector, BikeListingStatus.PENDING);
        long reviewingCount = bikeListingRepository.countByInspectorAndStatus(inspector, BikeListingStatus.REVIEWING);
        long approvedCount = bikeListingRepository.countByInspectorAndStatus(inspector, BikeListingStatus.APPROVED);
        long rejectedCount = bikeListingRepository.countByInspectorAndStatus(inspector, BikeListingStatus.REJECTED);
        long disputeCount = disputeRepository.countByStatus(DisputeStatus.OPEN)
                + disputeRepository.countByStatus(DisputeStatus.IN_PROGRESS);

        return new InspectorDashboardStatsResponse(pendingCount, reviewingCount, approvedCount, rejectedCount,
                disputeCount);
    }

    /**
     * S-21: Get listings for review - only listings assigned to this inspector
     */
    public Page<SellerListingResponse> getListingsForReview(Integer inspectorId, String status, String sort, int page,
            int pageSize) {
        User inspector = userRepository.findById(inspectorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy kiểm duyệt viên"));

        Sort.Direction direction = "oldest".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, "createdAt"));

        Page<BikeListing> result;

        if ("PENDING".equalsIgnoreCase(status)) {
            result = bikeListingRepository.findByInspectorOrUnassignedAndStatus(inspector, BikeListingStatus.PENDING,
                    pageable);
        } else if ("REVIEWING".equalsIgnoreCase(status)) {
            result = bikeListingRepository.findByInspectorAndStatus(inspector, BikeListingStatus.REVIEWING, pageable);
        } else {
            // ALL - get PENDING and REVIEWING assigned to this inspector, plus unassigned
            // PENDING
            result = bikeListingRepository.findByInspectorOrUnassignedAndStatusIn(
                    inspector,
                    java.util.List.of(BikeListingStatus.PENDING, BikeListingStatus.REVIEWING),
                    pageable);
        }

        return result.map(SellerListingResponse::from);
    }

    /**
     * S-22/S-23: Get listing detail for review
     */
    public PreviewListingResponse getListingDetail(Integer listingId) {
        BikeListing listing = bikeListingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tin đăng"));

        PreviewListingResponse response = PreviewListingResponse.from(listing);

        // Fetch listing images
        List<String> imageUrls = listingImageRepository.findByBikeListingOrderByImageOrder(listing)
                .stream()
                .map(img -> img.getImagePath())
                .toList();
        response.imageUrls = imageUrls;

        return response;
    }

    /**
     * S-22: Lock listing for review
     * Only the assigned inspector can lock the listing
     */
    public BikeListingResponse lockListing(Integer listingId, Integer inspectorId) {
        BikeListing listing = bikeListingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tin đăng"));

        if (listing.getStatus() != BikeListingStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ tin đăng PENDING mới có thể khóa");
        }

        // Verify listing is assigned to this inspector
        if (listing.getInspector() == null || !Objects.equals(listing.getInspector().getUserId(), inspectorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tin đăng này không được giao cho bạn");
        }

        // Lock the listing
        listing.setStatus(BikeListingStatus.REVIEWING);

        BikeListing saved = bikeListingRepository.save(listing);
        return BikeListingResponse.from(saved);
    }

    /**
     * S-22: Unlock listing
     * Only the assigned inspector can unlock. Listing goes back to PENDING but
     * stays assigned.
     */
    public BikeListingResponse unlockListing(Integer listingId, Integer inspectorId) {
        BikeListing listing = bikeListingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tin đăng"));

        if (listing.getStatus() != BikeListingStatus.REVIEWING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ tin đăng REVIEWING mới có thể mở khóa");
        }

        // Verify listing is assigned to this inspector
        if (listing.getInspector() == null || !Objects.equals(listing.getInspector().getUserId(), inspectorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tin đăng này không được giao cho bạn");
        }

        // Revert to PENDING, keep inspector assignment
        listing.setStatus(BikeListingStatus.PENDING);

        BikeListing saved = bikeListingRepository.save(listing);
        return BikeListingResponse.from(saved);
    }

    /**
     * S-23: Approve listing
     * Inspector can approve directly from PENDING (no REVIEWING lock needed).
     * Inspector must provide a reason for approval. An InspectionReport is created.
     */
    public BikeListingResponse approveListing(Integer listingId, Integer inspectorId,
            String reasonCode, String reasonText, String note) {
        BikeListing listing = bikeListingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tin đăng"));

        User inspector = userRepository.findById(inspectorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy kiểm duyệt viên"));

        if (listing.getInspector() != null && !Objects.equals(listing.getInspector().getUserId(), inspectorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Chỉ kiểm duyệt viên được giao mới có thể duyệt tin đăng này");
        }

        if (listing.getStatus() != BikeListingStatus.PENDING && listing.getStatus() != BikeListingStatus.REVIEWING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ tin đăng PENDING mới có thể duyệt");
        }

        // Validate reason
        if (reasonText == null || reasonText.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lý do là bắt buộc khi duyệt");
        }

        // Approve the listing and ensure inspector is set
        listing.setStatus(BikeListingStatus.APPROVED);
        listing.setInspector(inspector);

        BikeListing savedListing = bikeListingRepository.save(listing);

        // Create InspectionReport to record the approval decision
        InspectionReport report = new InspectionReport(
                savedListing, inspector, "APPROVED",
                reasonCode != null ? reasonCode : "MEETS_STANDARDS",
                reasonText, note);
        inspectionReportRepository.save(report);

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
     * Inspector can reject directly from PENDING (no REVIEWING lock needed).
     * Inspector must provide a reason for rejection. An InspectionReport is
     * created.
     */
    public BikeListingResponse rejectListing(Integer listingId, Integer inspectorId, String reasonCode,
            String reasonText, String note) {
        BikeListing listing = bikeListingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tin đăng"));

        User inspector = userRepository.findById(inspectorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy kiểm duyệt viên"));

        if (listing.getInspector() != null && !Objects.equals(listing.getInspector().getUserId(), inspectorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Chỉ kiểm duyệt viên được giao mới có thể từ chối tin đăng này");
        }

        if (listing.getStatus() != BikeListingStatus.PENDING && listing.getStatus() != BikeListingStatus.REVIEWING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ tin đăng PENDING mới có thể từ chối");
        }

        // Validate reason
        if (reasonText == null || reasonText.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lý do là bắt buộc khi từ chối");
        }
        if (reasonCode == null || reasonCode.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã lý do là bắt buộc khi từ chối");
        }

        // Reject the listing and set inspector
        listing.setStatus(BikeListingStatus.REJECTED);
        listing.setInspector(inspector);

        BikeListing saved = bikeListingRepository.save(listing);

        // Create InspectionReport to record the rejection decision
        InspectionReport report = new InspectionReport(
                saved, inspector, "REJECTED",
                reasonCode, reasonText, note);
        inspectionReportRepository.save(report);

        return BikeListingResponse.from(saved);
    }

    /**
     * S-24: Get review history - lấy tất cả listing mà inspector đã xử lý
     * (APPROVED, REJECTED, REVIEWING)
     */
    public Page<BikeListingResponse> getReviewHistory(Integer inspectorId, String from, String to, int page,
            int pageSize) {
        User inspector = userRepository.findById(inspectorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy kiểm duyệt viên"));

        // Sắp xếp theo updatedAt (mới nhất trước)
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        // Query listing theo inspector
        Page<BikeListing> listings = bikeListingRepository.findByInspector(inspector, pageable);

        // Filter theo date range nếu có (filter trên content của Page, không trên Page
        // object)
        if ((from != null && !from.isEmpty()) || (to != null && !to.isEmpty())) {
            LocalDateTime fromDate = from != null && !from.isEmpty()
                    ? LocalDate.parse(from).atStartOfDay()
                    : LocalDateTime.MIN;
            LocalDateTime toDate = to != null && !to.isEmpty()
                    ? LocalDate.parse(to).atTime(LocalTime.MAX)
                    : LocalDateTime.now();

            // Tạo list mới sau khi filter
            var filteredContent = listings.getContent().stream()
                    .filter(listing -> (listing.getUpdatedAt().isAfter(fromDate)
                            || listing.getUpdatedAt().isEqual(fromDate)) &&
                            (listing.getUpdatedAt().isBefore(toDate) || listing.getUpdatedAt().isEqual(toDate)))
                    .toList();

            // Convert to response
            return listings.map(BikeListingResponse::from);
        }

        // Convert to response
        return listings.map(BikeListingResponse::from);
    }

    /**
     * S-24: Get review detail - returns InspectionReport for a listing
     */
    public InspectionReportResponse getReviewDetail(Integer listingId) {
        BikeListing listing = bikeListingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tin đăng"));

        InspectionReport report = inspectionReportRepository.findTopByListingOrderByCreatedAtDesc(listing)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No inspection report found for this listing"));

        return InspectionReportResponse.from(report);
    }

    /**
     * Get the latest InspectionReport for a listing (used by seller to see
     * rejection/approval reason)
     */
    public InspectionReportResponse getInspectionReportByListing(Integer listingId) {
        BikeListing listing = bikeListingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tin đăng"));

        InspectionReport report = inspectionReportRepository.findTopByListingOrderByCreatedAtDesc(listing)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No inspection report found for this listing"));

        return InspectionReportResponse.from(report);
    }

    /**
     * Get disputes (delegated to DisputeService via repository)
     */
    public Page<DisputeListRowResponse> getDisputes(String status, int page, int pageSize) {
        DisputeStatus statusEnum = null;
        if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            try {
                statusEnum = DisputeStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore invalid status
            }
        }

        Sort.Direction direction = Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, "createdAt"));

        var disputes = disputeRepository.findByFilters(statusEnum, null, pageable);
        return disputes.map(DisputeListRowResponse::from);
    }

    /**
     * Get dispute detail
     */
    public DisputeDetailResponse getDisputeDetail(Integer disputeId) {
        var dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khiếu nại"));
        return DisputeDetailResponse.from(dispute);
    }
}
