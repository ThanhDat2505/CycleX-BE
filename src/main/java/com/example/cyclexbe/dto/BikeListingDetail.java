package com.example.cyclexbe.dto;

import java.math.BigDecimal;
import java.util.List;

public class BikeListingDetail {
    private Integer listingId;
    private String title;
    private String description;
    private BigDecimal price;
    private String locationCity;
    private String bikeType;
    private String brand;
    private Integer viewsCount;
    private List<String> images;

    public BikeListingDetail() {
    }

    public BikeListingDetail(Integer listingId, String title, String description, BigDecimal price, String locationCity, String bikeType, String brand, Integer viewsCount) {
        this.listingId = listingId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.locationCity = locationCity;
        this.bikeType = bikeType;
        this.brand = brand;
        this.viewsCount = viewsCount;
    }

    public BikeListingDetail(Integer listingId, String title, String description, BigDecimal price, String locationCity, String bikeType, String brand, Integer viewsCount, List<String> images) {
        this.listingId = listingId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.locationCity = locationCity;
        this.bikeType = bikeType;
        this.brand = brand;
        this.viewsCount = viewsCount;
        this.images = images;
    }

    public Integer getListingId() {
        return listingId;
    }

    public void setListingId(Integer listingId) {
        this.listingId = listingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public void setLocationCity(String locationCity) {
        this.locationCity = locationCity;
    }

    public String getBikeType() {
        return bikeType;
    }

    public void setBikeType(String bikeType) {
        this.bikeType = bikeType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Integer getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(Integer viewsCount) {
        this.viewsCount = viewsCount;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
