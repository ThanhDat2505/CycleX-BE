package com.example.cyclexbe.service;

import com.example.cyclexbe.dto.BikeListingCreateRequest;
import com.example.cyclexbe.dto.BikeListingResponse;
import com.example.cyclexbe.dto.BikeListingUpdateRequest;
import com.example.cyclexbe.domain.enums.BikeListingStatus;
import com.example.cyclexbe.entity.BikeListing;
import com.example.cyclexbe.entity.ListingImage;
import com.example.cyclexbe.entity.ListingVideo;
import com.example.cyclexbe.entity.Product;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.repository.BikeListingRepository;
import com.example.cyclexbe.repository.ListingImageRepository;
import com.example.cyclexbe.repository.ListingVideoRepository;
import com.example.cyclexbe.repository.ProductRepository;
import com.example.cyclexbe.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.criteria.Root;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BikeListingService {

    private final BikeListingRepository bikeListingRepository;
    private final ProductRepository productRepository;
    private final ListingImageRepository listingImageRepository;
    private final ListingVideoRepository listingVideoRepository;
    private final UserRepository userRepository;
    private final InspectorAssignmentService inspectorAssignmentService;

    public BikeListingService(BikeListingRepository bikeListingRepository,
            ProductRepository productRepository,
            ListingImageRepository listingImageRepository,
            ListingVideoRepository listingVideoRepository,
            UserRepository userRepository,
            InspectorAssignmentService inspectorAssignmentService) {
        this.bikeListingRepository = bikeListingRepository;
        this.productRepository = productRepository;
        this.listingImageRepository = listingImageRepository;
        this.listingVideoRepository = listingVideoRepository;
        this.userRepository = userRepository;
        this.inspectorAssignmentService = inspectorAssignmentService;
    }

    public BikeListingResponse create(BikeListingCreateRequest req, Integer authenticatedUserId) {
        // Ownership check: seller can only create listings for themselves
        if (!req.sellerId.equals(authenticatedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Bạn chỉ có thể tạo tin đăng cho tài khoản của mình");
        }

        User seller = userRepository.findById(req.sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người bán"));

        BikeListing b = new BikeListing();
        b.setSeller(seller);
        b.setTitle(req.title);
        b.setDescription(req.description);
        b.setBikeType(req.bikeType);
        b.setBrand(req.brand);
        b.setModel(req.model);
        b.setManufactureYear(req.manufactureYear);
        b.setCondition(req.condition);
        b.setUsageTime(req.usageTime);
        b.setReasonForSale(req.reasonForSale);
        b.setPrice(req.price);
        b.setLocationCity(req.locationCity);
        b.setPickupAddress(req.pickupAddress);
        if (req.status != null)
            b.setStatus(req.status);

        // Auto-assign inspector if listing is created with PENDING status
        if (b.getStatus() == BikeListingStatus.PENDING) {
            inspectorAssignmentService.assignInspector(b);
        }

        BikeListing saved = bikeListingRepository.save(b);
        return mapToResponse(saved);
    }

    public Page<BikeListingResponse> getAll(int page, int size, @SuppressWarnings("unused") BikeListingStatus status, String city, String title,
            List<String> bikeType, List<String> brand, List<String> condition,
            Double minPrice, Double maxPrice, String sortBy) {
        Sort sort;
        switch (sortBy != null ? sortBy : "newest") {
            case "priceAsc":
                sort = Sort.by(Sort.Direction.ASC, "price");
                break;
            case "priceDesc":
                sort = Sort.by(Sort.Direction.DESC, "price");
                break;
            case "mostViewed":
                sort = Sort.by(Sort.Direction.DESC, "viewsCount");
                break;
            case "newest":
            default:
                sort = Sort.by(Sort.Direction.DESC, "createdAt");
                break;
        }
        Pageable pageable = PageRequest.of(page, size, sort);

        // Public browse always restricts to APPROVED only (never expose DELETED/DRAFT/etc).
        // Explicit status filter is ignored for safety — buyer sees only what's available.
        final BikeListingStatus effectiveStatus = BikeListingStatus.APPROVED;

        Specification<BikeListing> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always filter to APPROVED for public browse
            predicates.add(cb.equal(root.get("status"), effectiveStatus));

            if (city != null && !city.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("locationCity")), "%" + city.toLowerCase() + "%"));
            }
            if (title != null && !title.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }
            if (bikeType != null && !bikeType.isEmpty()) {
                List<String> lowerTypes = bikeType.stream().map(String::toLowerCase).toList();
                predicates.add(cb.lower(root.get("bikeType")).in(lowerTypes));
            }
            if (brand != null && !brand.isEmpty()) {
                List<String> lowerBrands = brand.stream().map(String::toLowerCase).toList();
                predicates.add(cb.lower(root.get("brand")).in(lowerBrands));
            }
            if (condition != null && !condition.isEmpty()) {
                List<String> lowerConditions = condition.stream().map(String::toLowerCase).toList();
                predicates.add(cb.lower(root.get("condition")).in(lowerConditions));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), BigDecimal.valueOf(minPrice)));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), BigDecimal.valueOf(maxPrice)));
            }

            // Only show listings whose product is AVAILABLE
            Subquery<Integer> productSubquery = query.subquery(Integer.class);
            Root<Product> productRoot = productSubquery.from(Product.class);
            productSubquery.select(productRoot.get("listing").get("listingId"))
                    .where(cb.equal(productRoot.get("status"), "AVAILABLE"));
            predicates.add(root.get("listingId").in(productSubquery));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<BikeListing> pageResult = bikeListingRepository.findAll(spec, pageable);
        return pageResult.map(this::mapToResponse);
    }

    public BikeListingResponse getById(Integer id) {
        BikeListing b = bikeListingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tin đăng xe"));
        // Public endpoint — only expose APPROVED listings
        if (b.getStatus() != BikeListingStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tin đăng xe");
        }
        return mapToResponse(b);
    }

    public BikeListingResponse update(Integer id, BikeListingUpdateRequest req, Integer authenticatedUserId) {
        BikeListing b = bikeListingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tin đăng xe"));

        // Ownership check: only the seller who owns this listing can update it
        if (!b.getSeller().getUserId().equals(authenticatedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn chỉ có thể cập nhật tin đăng của mình");
        }

        if (req.title != null)
            b.setTitle(req.title);
        if (req.description != null)
            b.setDescription(req.description);
        if (req.bikeType != null)
            b.setBikeType(req.bikeType);
        if (req.brand != null)
            b.setBrand(req.brand);
        if (req.model != null)
            b.setModel(req.model);
        if (req.manufactureYear != null)
            b.setManufactureYear(req.manufactureYear);
        if (req.condition != null)
            b.setCondition(req.condition);
        if (req.usageTime != null)
            b.setUsageTime(req.usageTime);
        if (req.reasonForSale != null)
            b.setReasonForSale(req.reasonForSale);
        if (req.price != null)
            b.setPrice(req.price);
        if (req.locationCity != null)
            b.setLocationCity(req.locationCity);
        if (req.pickupAddress != null)
            b.setPickupAddress(req.pickupAddress);
        if (req.status != null)
            b.setStatus(req.status);

        BikeListing saved = bikeListingRepository.save(b);
        return mapToResponse(saved);
    }

    public void delete(Integer id, Integer authenticatedUserId) {
        BikeListing b = bikeListingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tin đăng xe"));

        // Ownership check: only the seller who owns this listing can delete it
        if (!b.getSeller().getUserId().equals(authenticatedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn chỉ có thể xóa tin đăng của mình");
        }

        b.setStatus(BikeListingStatus.REJECTED);
        bikeListingRepository.save(b);
    }

    private BikeListingResponse mapToResponse(BikeListing listing) {
        Optional<Product> productOpt = productRepository.findByListing_ListingId(listing.getListingId());
        Integer productId = productOpt.map(Product::getProductId).orElse(null);
        String productStatus = productOpt.map(Product::getStatus).orElse(null);

        List<String> imagePaths = listingImageRepository.findByBikeListingOrderByImageOrder(listing)
                .stream()
                .map(ListingImage::getImagePath)
                .filter(path -> path != null && !path.isBlank())
                .collect(Collectors.toList());

        String videoUrl = listingVideoRepository.findByBikeListing(listing)
                .map(ListingVideo::getVideoPath)
                .orElse(null);

        return BikeListingResponse.from(listing, productId, imagePaths, productStatus, videoUrl);
    }
}
