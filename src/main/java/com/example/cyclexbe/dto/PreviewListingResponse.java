package com.example.cyclexbe.dto;

import com.example.cyclexbe.domain.enums.BikeListingStatus;
import com.example.cyclexbe.entity.BikeListing;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PreviewListingResponse {
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
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public PreviewListingResponse() {}

    public static PreviewListingResponse from(BikeListing b) {
        if (b == null) return null;
        PreviewListingResponse r = new PreviewListingResponse();
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
        r.createdAt = b.getCreatedAt();
        r.updatedAt = b.getUpdatedAt();
        return r;
    }
}
