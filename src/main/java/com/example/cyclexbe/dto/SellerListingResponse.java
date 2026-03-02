package com.example.cyclexbe.dto;

import com.example.cyclexbe.domain.enums.BikeListingStatus;
import com.example.cyclexbe.entity.BikeListing;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SellerListingResponse {
    public Integer listingId;
    public String title;
    public String brand;
    public String model;
    public BigDecimal price;
    public BikeListingStatus status;
    public Integer viewsCount;
    public Integer inspectorId;
    public String inspectorName;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public SellerListingResponse() {}

    public SellerListingResponse(Integer listingId, String title, String brand, String model, BigDecimal price,
                                 BikeListingStatus status, Integer viewsCount, Integer inspectorId, String inspectorName,
                                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.listingId = listingId;
        this.title = title;
        this.brand = brand;
        this.model = model;
        this.price = price;
        this.status = status;
        this.viewsCount = viewsCount;
        this.inspectorId = inspectorId;
        this.inspectorName = inspectorName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static SellerListingResponse from(BikeListing b) {
        if (b == null) return null;
        return new SellerListingResponse(
                b.getListingId(),
                b.getTitle(),
                b.getBrand(),
                b.getModel(),
                b.getPrice(),
                b.getStatus(),
                b.getViewsCount(),
                b.getInspector() != null ? b.getInspector().getUserId() : null,
                b.getInspector() != null ? b.getInspector().getFullName() : null,
                b.getCreatedAt(),
                b.getUpdatedAt()
        );
    }
}
