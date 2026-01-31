package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public class GetListingsRequest {
    @NotNull(message = "Seller ID is required")
    public Integer sellerId;

    // Filter by status
    public String status;

    // Sort order
    public String sort;

    // Filter by text fields
    public String title;
    public String brand;
    public String model;

    // Filter by price range
    public BigDecimal minPrice;
    public BigDecimal maxPrice;

    // Pagination
    @Min(value = 0, message = "Page must be >= 0")
    public Integer page = 0;

    @Min(value = 1, message = "Page size must be >= 1")
    public Integer pageSize = 10;

    public GetListingsRequest() {}
}
