package com.example.cyclexbe.controller;

import com.example.cyclexbe.dto.BikeListingCreateRequest;
import com.example.cyclexbe.dto.BikeListingResponse;
import com.example.cyclexbe.dto.BikeListingUpdateRequest;
import com.example.cyclexbe.domain.enums.BikeListingStatus;
import com.example.cyclexbe.service.BikeListingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bikelistings")
public class BikeListingController {

    private final BikeListingService bikeListingService;

    public BikeListingController(BikeListingService bikeListingService) {
        this.bikeListingService = bikeListingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BikeListingResponse create(@Valid @RequestBody BikeListingCreateRequest req) {
        return bikeListingService.create(req);
    }

    @GetMapping
    public Page<BikeListingResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) BikeListingStatus status,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String title
    ) {
        return bikeListingService.getAll(page, size, status, city, title);
    }

    @GetMapping("/{id}")
    public BikeListingResponse getById(@PathVariable Integer id) {
        return bikeListingService.getById(id);
    }

    @PutMapping("/{id}")
    public BikeListingResponse update(@PathVariable Integer id, @Valid @RequestBody BikeListingUpdateRequest req) {
        return bikeListingService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        bikeListingService.delete(id);
    }
}
