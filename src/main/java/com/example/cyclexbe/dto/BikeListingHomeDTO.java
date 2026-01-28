package com.example.cyclexbe.dto;

import java.math.BigDecimal;
public class BikeListingHomeDTO {
    private Integer listingId;
    private String title;
    private BigDecimal price;
    private String imageUrl;
    private String locationCity;
    private Integer viewCount;

    public BikeListingHomeDTO() {
    }

    public BikeListingHomeDTO(Integer listingId, String title, BigDecimal price, String imageUrl, String locationCity, Integer viewCount) {
        this.listingId = listingId;
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;
        this.locationCity = locationCity;
        this.viewCount = viewCount;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public void setLocationCity(String locationCity) {
        this.locationCity = locationCity;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
}
