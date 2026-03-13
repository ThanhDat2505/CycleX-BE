package com.example.cyclexbe.controller;

import com.example.cyclexbe.dto.BikeListingCreateRequest;
import com.example.cyclexbe.dto.BikeListingResponse;
import com.example.cyclexbe.dto.BikeListingUpdateRequest;
import com.example.cyclexbe.domain.enums.BikeListingStatus;
import com.example.cyclexbe.service.BikeListingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bikelistings")
public class BikeListingController {

    private final BikeListingService bikeListingService;

    public BikeListingController(BikeListingService bikeListingService) {
        this.bikeListingService = bikeListingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BikeListingResponse create(Authentication authentication, @Valid @RequestBody BikeListingCreateRequest req) {
        Integer authenticatedUserId = Integer.parseInt(authentication.getPrincipal().toString());
        return bikeListingService.create(req, authenticatedUserId);
    }

    @GetMapping
    public Page<BikeListingResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) BikeListingStatus status,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> bikeType,
            @RequestParam(required = false) List<String> brand,
            @RequestParam(required = false) List<String> condition,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "newest") String sortBy
    ) {
        return bikeListingService.getAll(page, size, status, city, title, bikeType, brand, condition, minPrice, maxPrice, sortBy);
    }

    @GetMapping("/{id}")
    public BikeListingResponse getById(@PathVariable Integer id) {
        return bikeListingService.getById(id);
    }

    @PutMapping("/{id}")
    public BikeListingResponse update(Authentication authentication, @PathVariable Integer id, @Valid @RequestBody BikeListingUpdateRequest req) {
        Integer authenticatedUserId = Integer.parseInt(authentication.getPrincipal().toString());
        return bikeListingService.update(id, req, authenticatedUserId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication authentication, @PathVariable Integer id) {
        Integer authenticatedUserId = Integer.parseInt(authentication.getPrincipal().toString());
        bikeListingService.delete(id, authenticatedUserId);
    }
}
