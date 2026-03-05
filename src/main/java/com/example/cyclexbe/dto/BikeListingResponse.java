package com.example.cyclexbe.dto;

import com.example.cyclexbe.entity.BikeListing;
import com.example.cyclexbe.domain.enums.BikeListingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class BikeListingResponse {
    public Integer listingId;
    public Integer sellerId;
    public Integer productId;
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
    public List<String> images;
    public String imageUrl;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public BikeListingResponse() {}

    public static BikeListingResponse from(BikeListing b) {
        return from(b, null, Collections.emptyList());
    }

    public static BikeListingResponse from(BikeListing b, Integer productId, List<String> imagePaths) {
        BikeListingResponse r = new BikeListingResponse();
        if (b == null) return r;
        r.listingId = b.getListingId();
        r.sellerId = b.getSeller() != null ? b.getSeller().getUserId() : null;
        r.productId = productId;
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
        r.images = imagePaths == null ? Collections.emptyList() : imagePaths;
        r.imageUrl = r.images.isEmpty() ? null : r.images.get(0);
        r.createdAt = b.getCreatedAt();
        r.updatedAt = b.getUpdatedAt();
        return r;
    }
}
