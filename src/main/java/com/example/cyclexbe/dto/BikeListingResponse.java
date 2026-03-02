package com.example.cyclexbe.dto;

import com.example.cyclexbe.entity.BikeListing;
import com.example.cyclexbe.domain.enums.BikeListingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BikeListingResponse {
    public Integer listingId;
    public Integer sellerId;
    public String title;
    public String description;
    public String bikeType;
    public String brand;
    public String model;
    public Integer manufactureYear;
    public String condition;
    public String usageTime;
    public String reasonForSale;
    public BigDecimal price;
    public String locationCity;
    public String pickupAddress;
    public BikeListingStatus status;
    public Integer viewsCount;
    public Integer inspectorId;
    public String inspectorName;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public BikeListingResponse() {}

    public static BikeListingResponse from(BikeListing b) {
        BikeListingResponse r = new BikeListingResponse();
        if (b == null) return r;
        r.listingId = b.getListingId();
        r.sellerId = b.getSeller() != null ? b.getSeller().getUserId() : null;
        r.title = b.getTitle();
        r.description = b.getDescription();
        r.bikeType = b.getBikeType();
        r.brand = b.getBrand();
        r.model = b.getModel();
        r.manufactureYear = b.getManufactureYear();
        r.condition = b.getCondition();
        r.usageTime = b.getUsageTime();
        r.reasonForSale = b.getReasonForSale();
        r.price = b.getPrice();
        r.locationCity = b.getLocationCity();
        r.pickupAddress = b.getPickupAddress();
        r.status = b.getStatus();
        r.viewsCount = b.getViewsCount();
        r.inspectorId = b.getInspector() != null ? b.getInspector().getUserId() : null;
        r.inspectorName = b.getInspector() != null ? b.getInspector().getFullName() : null;
        r.createdAt = b.getCreatedAt();
        r.updatedAt = b.getUpdatedAt();
        return r;
    }
}
