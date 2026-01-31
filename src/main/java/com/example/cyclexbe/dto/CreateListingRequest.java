package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public class CreateListingRequest {
    @NotNull(message = "Seller ID is required")
    public Integer sellerId;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be <= 255 characters")
    public String title;

    public String description;

    @NotBlank(message = "Bike type is required")
    @Size(max = 50, message = "Bike type must be <= 50 characters")
    public String bikeType;

    @NotBlank(message = "Brand is required")
    @Size(max = 100, message = "Brand must be <= 100 characters")
    public String brand;

    @NotBlank(message = "Model is required")
    @Size(max = 100, message = "Model must be <= 100 characters")
    public String model;

    public Integer manufactureYear;

    @Size(max = 50, message = "Condition must be <= 50 characters")
    public String condition;

    @Size(max = 100, message = "Usage time must be <= 100 characters")
    public String usageTime;

    public String reasonForSale;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be >= 0")
    public BigDecimal price;

    @Size(max = 100, message = "Location city must be <= 100 characters")
    public String locationCity;

    public String pickupAddress;

    public Boolean saveDraft = true;

    public CreateListingRequest() {}
}
