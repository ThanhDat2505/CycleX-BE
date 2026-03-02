package com.example.cyclexbe.service;

import com.example.cyclexbe.dto.BikeListingCreateRequest;
import com.example.cyclexbe.dto.BikeListingResponse;
import com.example.cyclexbe.dto.BikeListingUpdateRequest;
import com.example.cyclexbe.domain.enums.BikeListingStatus;
import com.example.cyclexbe.entity.BikeListing;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.repository.BikeListingRepository;
import com.example.cyclexbe.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BikeListingService {

    private final BikeListingRepository bikeListingRepository;
    private final UserRepository userRepository;
    private final InspectorAssignmentService inspectorAssignmentService;

    public BikeListingService(BikeListingRepository bikeListingRepository, UserRepository userRepository,
                              InspectorAssignmentService inspectorAssignmentService) {
        this.bikeListingRepository = bikeListingRepository;
        this.userRepository = userRepository;
        this.inspectorAssignmentService = inspectorAssignmentService;
    }

    public BikeListingResponse create(BikeListingCreateRequest req) {
        User seller = userRepository.findById(req.sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));

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
        if (req.status != null) b.setStatus(req.status);

        // Auto-assign inspector if listing is created with PENDING status
        if (b.getStatus() == BikeListingStatus.PENDING) {
            inspectorAssignmentService.assignInspector(b);
        }

        BikeListing saved = bikeListingRepository.save(b);
        return BikeListingResponse.from(saved);
    }

    public Page<BikeListingResponse> getAll(int page, int size, BikeListingStatus status, String city, String title) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BikeListing> pageResult;
        if (status != null) {
            pageResult = bikeListingRepository.findByStatus(status, pageable);
        } else if (city != null) {
            pageResult = bikeListingRepository.findByLocationCityContainingIgnoreCase(city, pageable);
        } else if (title != null) {
            pageResult = bikeListingRepository.findByTitleContainingIgnoreCase(title, pageable);
        } else {
            pageResult = bikeListingRepository.findAll(pageable);
        }
        return pageResult.map(BikeListingResponse::from);
    }

    public BikeListingResponse getById(Integer id) {
        BikeListing b = bikeListingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BikeListing not found"));
        return BikeListingResponse.from(b);
    }

    public BikeListingResponse update(Integer id, BikeListingUpdateRequest req) {
        BikeListing b = bikeListingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BikeListing not found"));

        if (req.title != null) b.setTitle(req.title);
        if (req.description != null) b.setDescription(req.description);
        if (req.bikeType != null) b.setBikeType(req.bikeType);
        if (req.brand != null) b.setBrand(req.brand);
        if (req.model != null) b.setModel(req.model);
        if (req.manufactureYear != null) b.setManufactureYear(req.manufactureYear);
        if (req.condition != null) b.setCondition(req.condition);
        if (req.usageTime != null) b.setUsageTime(req.usageTime);
        if (req.reasonForSale != null) b.setReasonForSale(req.reasonForSale);
        if (req.price != null) b.setPrice(req.price);
        if (req.locationCity != null) b.setLocationCity(req.locationCity);
        if (req.pickupAddress != null) b.setPickupAddress(req.pickupAddress);
        if (req.status != null) b.setStatus(req.status);

        BikeListing saved = bikeListingRepository.save(b);
        return BikeListingResponse.from(saved);
    }

    public void delete(Integer id) {
        BikeListing b = bikeListingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BikeListing not found"));
        b.setStatus(BikeListingStatus.REJECTED);
        bikeListingRepository.save(b);
    }
}
