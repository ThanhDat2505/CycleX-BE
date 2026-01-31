package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.example.cyclexbe.domain.enums.BikeListingStatus;
import java.math.BigDecimal;

public class UpdateListingRequest {
    @NotNull(message = "Seller ID is required")
    public Integer sellerId;

    @Size(max = 255, message = "Title must be <= 255 characters")
    public String title;

    public String description;

    @Size(max = 50, message = "Bike type must be <= 50 characters")
    public String bikeType;

    @Size(max = 100, message = "Brand must be <= 100 characters")
    public String brand;

    @Size(max = 100, message = "Model must be <= 100 characters")
    public String model;

    public Integer manufactureYear;

    @Size(max = 50, message = "Condition must be <= 50 characters")
    public String condition;

    @Size(max = 100, message = "Usage time must be <= 100 characters")
    public String usageTime;

    public String reasonForSale;

    public BigDecimal price;

    @Size(max = 100, message = "Location city must be <= 100 characters")
    public String locationCity;

    public String pickupAddress;

    public BikeListingStatus status;

    public UpdateListingRequest() {}
}
