package com.example.cyclexbe.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.example.cyclexbe.domain.enums.BikeListingStatus;

public class BikeListingCreateRequest {

    @NotNull(message = "sellerId is required")
    public Integer sellerId;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title is too long")
    public String title;

    public String description;

    @Size(max = 50)
    public String bikeType;

    @Size(max = 100)
    public String brand;

    @Size(max = 100)
    public String model;

    public Integer manufactureYear;

    @Size(max = 50)
    public String condition;

    @Size(max = 100)
    public String usageTime;

    public String reasonForSale;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be zero or positive")
    public BigDecimal price;

    @Size(max = 100)
    public String locationCity;

    public String pickupAddress;

    public BikeListingStatus status; // optional

    public BikeListingCreateRequest() {}
}
