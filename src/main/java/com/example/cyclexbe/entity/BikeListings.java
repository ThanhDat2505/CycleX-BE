package com.example.cyclexbe.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
    @Table(name = "bike_listings")
    public class BikeListings {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer listingId;

        @ManyToOne
        @JoinColumn(name = "seller_id")
        private User seller;

        private String title;
        private String description;

        private String bikeType;
        private String brand;
        private String model;

        private Integer manufactureYear;
        private String condition;
        private String usageTime;

        private BigDecimal price;

        private String locationCity;
        private String pickupAddress;

        private String status; // ACTIVE / DRAFT / SOLD

        private Integer viewsCount;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public BikeListings() {
        }

        public BikeListings(Integer listingId, User seller, String title, String description, String bikeType, String brand, String model, Integer manufactureYear, String condition, String usageTime, BigDecimal price, String locationCity, String pickupAddress, String status, Integer viewsCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.listingId = listingId;
            this.seller = seller;
            this.title = title;
            this.description = description;
            this.bikeType = bikeType;
            this.brand = brand;
            this.model = model;
            this.manufactureYear = manufactureYear;
            this.condition = condition;
            this.usageTime = usageTime;
            this.price = price;
            this.locationCity = locationCity;
            this.pickupAddress = pickupAddress;
            this.status = status;
            this.viewsCount = viewsCount;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
        @PrePersist
        protected void onCreate() {
            LocalDateTime now = LocalDateTime.now();
            this.createdAt = now;
            this.updatedAt = now;
        }

        @PreUpdate
        protected void onUpdate() {
            this.updatedAt = LocalDateTime.now();
        }

        public BikeListings(User seller, String title, String description, String bikeType, String brand, String model, Integer manufactureYear, String condition, String usageTime, BigDecimal price, String locationCity, String pickupAddress, String status, Integer viewsCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.seller = seller;
            this.title = title;
            this.description = description;
            this.bikeType = bikeType;
            this.brand = brand;
            this.model = model;
            this.manufactureYear = manufactureYear;
            this.condition = condition;
            this.usageTime = usageTime;
            this.price = price;
            this.locationCity = locationCity;
            this.pickupAddress = pickupAddress;
            this.status = status;
            this.viewsCount = viewsCount;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public Integer getListingId() {
            return listingId;
        }

        public void setListingId(Integer listingId) {
            this.listingId = listingId;
        }

        public User getSeller() {
            return seller;
        }

        public void setSeller(User seller) {
            this.seller = seller;
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

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public Integer getManufactureYear() {
            return manufactureYear;
        }

        public void setManufactureYear(Integer manufactureYear) {
            this.manufactureYear = manufactureYear;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public String getUsageTime() {
            return usageTime;
        }

        public void setUsageTime(String usageTime) {
            this.usageTime = usageTime;
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

        public String getPickupAddress() {
            return pickupAddress;
        }

        public void setPickupAddress(String pickupAddress) {
            this.pickupAddress = pickupAddress;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getViewsCount() {
            return viewsCount;
        }

        public void setViewsCount(Integer viewsCount) {
            this.viewsCount = viewsCount;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }

        @OneToMany(mappedBy = "bikeListing", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<BikeImage> images = new ArrayList<>();
}
