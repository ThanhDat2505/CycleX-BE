package com.example.cyclexbe.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import com.example.cyclexbe.domain.enums.BikeListingStatus;

public class BikeListingUpdateRequest {
    @Size(max = 255)
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

    @PositiveOrZero(message = "Price must be zero or positive")
    public BigDecimal price;

    @Size(max = 100)
    public String locationCity;

    public String pickupAddress;

    public BikeListingStatus status;

    public BikeListingUpdateRequest() {}
}
