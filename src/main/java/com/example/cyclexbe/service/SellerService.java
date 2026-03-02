package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.BikeListingStatus;
import com.example.cyclexbe.dto.*;
import org.springframework.transaction.annotation.Transactional;
import com.example.cyclexbe.entity.BikeListing;
import com.example.cyclexbe.entity.InspectionReport;
import com.example.cyclexbe.entity.ListingImage;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.repository.BikeListingRepository;
import com.example.cyclexbe.repository.InspectionReportRepository;
import com.example.cyclexbe.repository.ListingImageRepository;
import com.example.cyclexbe.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class SellerService {

    private final BikeListingRepository bikeListingRepository;
    private final UserRepository userRepository;
    private final ListingImageRepository listingImageRepository;
    private final InspectorAssignmentService inspectorAssignmentService;
    private final InspectionReportRepository inspectionReportRepository;

    public SellerService(BikeListingRepository bikeListingRepository, UserRepository userRepository,
                         ListingImageRepository listingImageRepository,
                         InspectorAssignmentService inspectorAssignmentService,
                         InspectionReportRepository inspectionReportRepository) {
        this.bikeListingRepository = bikeListingRepository;
        this.userRepository = userRepository;
        this.listingImageRepository = listingImageRepository;
        this.inspectorAssignmentService = inspectorAssignmentService;
        this.inspectionReportRepository = inspectionReportRepository;
    }

    /**
     * S-10: Get seller dashboard statistics
     */
    public SellerDashboardStatsResponse getDashboardStats(Integer sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));

        long approvedCount = bikeListingRepository.countBySellerAndStatus(seller, BikeListingStatus.APPROVED);
        long pendingCount = bikeListingRepository.countBySellerAndStatus(seller, BikeListingStatus.PENDING);
        long rejectedCount = bikeListingRepository.countBySellerAndStatus(seller, BikeListingStatus.REJECTED);
        long totalListings = bikeListingRepository.countBySellerAndStatus(seller, BikeListingStatus.APPROVED) +
                bikeListingRepository.countBySellerAndStatus(seller, BikeListingStatus.PENDING) +
                bikeListingRepository.countBySellerAndStatus(seller, BikeListingStatus.REJECTED);

        // Calculate total views from all approved listings
        long totalViews = bikeListingRepository.findBySellerAndStatus(seller, BikeListingStatus.APPROVED, Pageable.unpaged())
                .stream()
                .mapToLong(BikeListing::getViewsCount)
                .sum();

        return new SellerDashboardStatsResponse(approvedCount, pendingCount, rejectedCount, totalListings, totalViews);
    }

    /**
     * S-11: Get seller's listings with filtering and pagination
     */
    public Page<SellerListingResponse> getSellerListings(Integer sellerId, String status, String title, String brand,
                                                         String model, BigDecimal minPrice, BigDecimal maxPrice,
                                                         String sort, int page, int pageSize) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));

        // Determine sort order
        Sort.Direction direction = "oldest".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, "createdAt"));

        // Build dynamic specification with multiple filters
        Specification<BikeListing> spec = buildSellerListingSpec(seller, status, title, brand, model, minPrice, maxPrice);

        return bikeListingRepository.findAll(spec, pageable).map(SellerListingResponse::from);
    }

    /**
     * Build Specification for dynamic filtering
     */
    private Specification<BikeListing> buildSellerListingSpec(User seller, String status, String title, String brand,
                                                              String model, BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by seller (required)
            predicates.add(cb.equal(root.get("seller"), seller));

            // Filter by status (optional)
            if (status != null && !status.isEmpty()) {
                BikeListingStatus bikeStatus = parseBikeListingStatus(status);
                predicates.add(cb.equal(root.get("status"), bikeStatus));
            }

            // Filter by title (optional) - case insensitive
            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            // Filter by brand (optional) - case insensitive
            if (brand != null && !brand.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("brand")), "%" + brand.toLowerCase() + "%"));
            }

            // Filter by model (optional) - case insensitive
            if (model != null && !model.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("model")), "%" + model.toLowerCase() + "%"));
            }

            // Filter by price range (optional)
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            // Combine all predicates with AND
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * S-11: Get listing detail for seller
     */
    public SellerListingResponse getListingDetail(Integer sellerId, Integer listingId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));

        BikeListing listing = bikeListingRepository.findByListingIdAndSeller(listingId, seller)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found or not owned by seller"));

        return SellerListingResponse.from(listing);
    }

    /**
     * S-11: Get rejection reason (if listing is rejected)
     */
    public SellerListingResponse getRejectionReason(Integer sellerId, Integer listingId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));

        BikeListing listing = bikeListingRepository.findByListingIdAndSeller(listingId, seller)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found or not owned by seller"));

        if (listing.getStatus() != BikeListingStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Listing is not rejected");
        }

        return SellerListingResponse.from(listing);
    }

    /**
     * Get listing result: listing info + inspection report (lý do approve/reject)
     * Dùng cho seller xem kết quả duyệt listing.
     * Chỉ trả về cho listing đã APPROVED hoặc REJECTED.
     */
    public ListingResultResponse getListingResult(Integer sellerId, Integer listingId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));

        BikeListing listing = bikeListingRepository.findByListingIdAndSeller(listingId, seller)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found or not owned by seller"));

        if (listing.getStatus() != BikeListingStatus.APPROVED && listing.getStatus() != BikeListingStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Listing has not been reviewed yet. Current status: " + listing.getStatus());
        }

        SellerListingResponse listingResponse = SellerListingResponse.from(listing);

        // Get latest inspection report (may be null if legacy data before this feature)
        InspectionReportResponse reportResponse = inspectionReportRepository
                .findTopByListingOrderByCreatedAtDesc(listing)
                .map(InspectionReportResponse::from)
                .orElse(null);

        return new ListingResultResponse(listingResponse, reportResponse);
    }

    /**
     * Helper method to parse status string to BikeListingStatus enum
     */
    private BikeListingStatus parseBikeListingStatus(String status) {
        try {
            return BikeListingStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + status);
        }
    }

    /**
     * S-12: Create new listing (as DRAFT by default)
     */
    public BikeListingResponse createListing(Integer sellerId, CreateListingRequest req) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));

        validateCreateListingRequest(req);

        BikeListing listing = new BikeListing();
        listing.setSeller(seller);
        listing.setTitle(req.title);
        listing.setDescription(req.description);
        listing.setBikeType(req.bikeType);
        listing.setBrand(req.brand);
        listing.setModel(req.model);
        listing.setManufactureYear(req.manufactureYear);
        listing.setCondition(req.condition);
        listing.setUsageTime(req.usageTime);
        listing.setReasonForSale(req.reasonForSale);
        listing.setPrice(req.price);
        listing.setLocationCity(req.locationCity);
        listing.setPickupAddress(req.pickupAddress);

        // Set status: DRAFT (if saveDraft=true) or PENDING (if saveDraft=false)
        if (req.saveDraft != null && !req.saveDraft) {
            listing.setStatus(BikeListingStatus.PENDING);
            // Auto-assign inspector (least-load strategy)
            inspectorAssignmentService.assignInspector(listing);
        } else {
            listing.setStatus(BikeListingStatus.DRAFT);
        }

        BikeListing saved = bikeListingRepository.save(listing);
        return BikeListingResponse.from(saved);
    }

    /**
     * S-12: Update existing listing (only DRAFT or REJECTED status allowed)
     * Seller can edit listing fields when status is DRAFT or REJECTED
     * @param sellerId - Current seller ID (validated by controller)
     * @param listingId - Listing ID to update
     * @param req - Update request with fields to modify
     * @return Updated BikeListingResponse
     */
    @Transactional
    public BikeListingResponse updateListing(Integer sellerId, Integer listingId, UpdateListingRequest req) {
        // Get seller
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));

        // Get listing and verify ownership
        BikeListing listing = bikeListingRepository.findByListingIdAndSeller(listingId, seller)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found or not owned by seller"));

        // Check if listing status allows editing (DRAFT or REJECTED only)
        if (listing.getStatus() != BikeListingStatus.DRAFT && listing.getStatus() != BikeListingStatus.REJECTED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot edit listing with status: " + listing.getStatus() + ". Only DRAFT or REJECTED listings can be edited."
            );
        }

        // Update fields if provided
        if (req.title != null && !req.title.isBlank()) {
            listing.setTitle(req.title);
        }
        if (req.description != null && !req.description.isBlank()) {
            listing.setDescription(req.description);
        }
        if (req.bikeType != null && !req.bikeType.isBlank()) {
            listing.setBikeType(req.bikeType);
        }
        if (req.brand != null && !req.brand.isBlank()) {
            listing.setBrand(req.brand);
        }
        if (req.model != null && !req.model.isBlank()) {
            listing.setModel(req.model);
        }
        if (req.manufactureYear != null) {
            listing.setManufactureYear(req.manufactureYear);
        }
        if (req.condition != null && !req.condition.isBlank()) {
            listing.setCondition(req.condition);
        }
        if (req.usageTime != null && !req.usageTime.isBlank()) {
            listing.setUsageTime(req.usageTime);
        }
        if (req.reasonForSale != null && !req.reasonForSale.isBlank()) {
            listing.setReasonForSale(req.reasonForSale);
        }
        if (req.price != null) {
            listing.setPrice(req.price);
        }
        if (req.locationCity != null && !req.locationCity.isBlank()) {
            listing.setLocationCity(req.locationCity);
        }
        if (req.pickupAddress != null && !req.pickupAddress.isBlank()) {
            listing.setPickupAddress(req.pickupAddress);
        }

        // Validate updated listing has required fields
        validateUpdateListingFields(listing);

        // Save updated listing
        BikeListing saved = bikeListingRepository.save(listing);
        return BikeListingResponse.from(saved);
    }

    /**
     * S-12: Submit listing for approval (DRAFT → PENDING)
     */
    public BikeListingResponse submitListing(Integer sellerId, Integer listingId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));

        BikeListing listing = bikeListingRepository.findByListingIdAndSeller(listingId, seller)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found or not owned by seller"));

        if (listing.getStatus() != BikeListingStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only DRAFT listings can be submitted");
        }

        validateSubmitListingFields(listing);

        // ✅ Validate minimum 3 images before submit
        List<ListingImage> images = listingImageRepository.findByBikeListingOrderByImageOrder(listing);
        if (images.size() < 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Listing must have at least 3 images. Current: %d/3", images.size()));
        }

        listing.setStatus(BikeListingStatus.PENDING);

        // Auto-assign inspector (least-load strategy)
        inspectorAssignmentService.assignInspector(listing);

        BikeListing saved = bikeListingRepository.save(listing);
        return BikeListingResponse.from(saved);
    }

    /**
     * S-14: Preview listing
     */
    public PreviewListingResponse previewListing(Integer sellerId, Integer listingId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));

        BikeListing listing = bikeListingRepository.findByListingIdAndSeller(listingId, seller)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found or not owned by seller"));

        return PreviewListingResponse.from(listing);
    }

    /**
     * S-18: Get all draft listings for seller
     */
    public Page<SellerListingResponse> getDraftListings(Integer sellerId, String sort, int page, int pageSize) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));

        Sort.Direction direction = "oldest".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, "createdAt"));

        return bikeListingRepository.findBySellerAndStatus(seller, BikeListingStatus.DRAFT, pageable)
                .map(SellerListingResponse::from);
    }

    /**
     * S-18: Delete draft listing
     */
    public void deleteDraft(Integer sellerId, Integer listingId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));

        BikeListing listing = bikeListingRepository.findByListingIdAndSeller(listingId, seller)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found or not owned by seller"));

        if (listing.getStatus() != BikeListingStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only DRAFT listings can be deleted");
        }
        listing.setStatus(BikeListingStatus.DELETED);
        bikeListingRepository.save(listing);
    }

    /**
     * Validate required fields for creating listing
     */
    private void validateCreateListingRequest(CreateListingRequest req) {
        if (req.title == null || req.title.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }
        if (req.price == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price is required");
        }
        if (req.bikeType == null || req.bikeType.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bike type is required");
        }
        if (req.brand == null || req.brand.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brand is required");
        }
        if (req.model == null || req.model.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Model is required");
        }
    }

    /**
     * Validate required fields before submission (DRAFT → PENDING)
     */
    private void validateSubmitListingFields(BikeListing listing) {
        if (listing.getTitle() == null || listing.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot submit: title is required");
        }
        if (listing.getPrice() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot submit: price is required");
        }
        if (listing.getBikeType() == null || listing.getBikeType().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot submit: bike type is required");
        }
        if (listing.getBrand() == null || listing.getBrand().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot submit: brand is required");
        }
        if (listing.getModel() == null || listing.getModel().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot submit: model is required");
        }
    }

    /**
     * Validate required fields for updating listing
     */
    private void validateUpdateListingFields(BikeListing listing) {
        if (listing.getTitle() == null || listing.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }
        if (listing.getPrice() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price is required");
        }
        if (listing.getBikeType() == null || listing.getBikeType().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bike type is required");
        }
        if (listing.getBrand() == null || listing.getBrand().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brand is required");
        }
        if (listing.getModel() == null || listing.getModel().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Model is required");
        }
    }

    /**
     * S-13: Upload listing image
     * FE upload ảnh, BE chỉ lưu path dẫn tới ảnh
     */
    public ListingImageResponse uploadListingImage(Integer sellerId, Integer listingId, UploadListingImageRequest req) {
        // Validate listing exists và thuộc seller này
        BikeListing listing = bikeListingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        if (!listing.getSeller().getUserId().equals(sellerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to upload images for this listing");
        }

        if (!listing.getStatus().equals(BikeListingStatus.DRAFT)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only DRAFT listings can have images");
        }

        // ✅ Validate maximum 10 images per listing
        long currentImageCount = listingImageRepository.countByBikeListing(listing);
        if (currentImageCount >= 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Maximum 10 images per listing. Current: %d/10", currentImageCount));
        }

        // Validate image path format: /public/{listingId}/xxx.png hoặc .jpg
        validateImagePath(req.imagePath, listingId);

        // Đếm ảnh hiện có để xác định imageOrder
        Integer imageOrder = (int) (currentImageCount + 1);

        // Tạo và lưu ListingImage entity
        ListingImage image = new ListingImage(listing, req.imagePath, imageOrder);
        ListingImage savedImage = listingImageRepository.save(image);

        return ListingImageResponse.from(savedImage);
    }

    /**
     * S-13: Get listing images
     */
    public List<ListingImageResponse> getListingImages(Integer sellerId, Integer listingId) {
        BikeListing listing = bikeListingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        if (!listing.getSeller().getUserId().equals(sellerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to view images for this listing");
        }

        return listingImageRepository.findByBikeListingOrderByImageOrder(listing)
                .stream()
                .map(ListingImageResponse::from)
                .toList();
    }

    /**
     * S-13: Delete listing image
     */
    public void deleteListingImage(Integer sellerId, Integer listingId, Integer imageId) {
        BikeListing listing = bikeListingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        if (!listing.getSeller().getUserId().equals(sellerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to delete images for this listing");
        }

        ListingImage image = listingImageRepository.findByImageIdAndBikeListing(imageId, listing)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));

        // ✅ Validate minimum 3 images - prevent deletion if it would leave less than 3
        List<ListingImage> allImages = listingImageRepository.findByBikeListingOrderByImageOrder(listing);
        if (allImages.size() <= 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Listing must have at least 3 images. Cannot delete - current: " + allImages.size());
        }

        listingImageRepository.delete(image);

        // Reorder images sau khi xóa
        reorderImages(listing);
    }

    /**
     * Reorder images sau khi xóa (tái sắp xếp imageOrder)
     */
    private void reorderImages(BikeListing listing) {
        List<ListingImage> images = listingImageRepository.findByBikeListingOrderByImageOrder(listing);
        for (int i = 0; i < images.size(); i++) {
            images.get(i).setImageOrder(i + 1);
        }
        listingImageRepository.saveAll(images);
    }

    /**
     * Validate image path format
     * Expected format: /public/{listingId}/[image_number].png/jpg
     */
    private void validateImagePath(String imagePath, Integer listingId) {
        if (imagePath == null || imagePath.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image path cannot be empty");
        }

        // Check path contains correct listingId
        String expectedPrefix = "/public/" + listingId + "/";
        if (!imagePath.startsWith(expectedPrefix)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Image path must start with /public/" + listingId + "/");
        }

        // Check file extension
        String lowerPath = imagePath.toLowerCase();
        if (!lowerPath.endsWith(".png") && !lowerPath.endsWith(".jpg") && !lowerPath.endsWith(".jpeg")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Image must be PNG, JPG, or JPEG format");
        }
    }
}


