package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.BikeListingStatus;
import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.entity.BikeListing;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.repository.BikeListingRepository;
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

    public SellerService(BikeListingRepository bikeListingRepository, UserRepository userRepository) {
        this.bikeListingRepository = bikeListingRepository;
        this.userRepository = userRepository;
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
        } else {
            listing.setStatus(BikeListingStatus.DRAFT);
        }

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

        listing.setStatus(BikeListingStatus.PENDING);
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

        bikeListingRepository.delete(listing);
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
     * Validate required fields before submission
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
}


