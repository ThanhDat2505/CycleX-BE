package com.example.cyclexbe.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;


    @Entity
    @Table(name = "BikeImages")
    public class BikeImage {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "image_id")
        private Integer imageId;

        // FK -> BikeListing
        @ManyToOne
        @JoinColumn(name = "listing_id", nullable = false)
        private BikeListings bikeListing;

        @Column(name = "image_type", length = 50)
        private String imageType;

        @Column(name = "image_url", length = 500, nullable = false)
        private String imageUrl;

        @Column(name = "is_main")
        private Boolean isMain = false;

        @Column(name = "uploaded_at")
        private LocalDateTime uploadedAt;

        public BikeImage() {
        }

        public BikeImage(BikeListings bikeListing, String imageType, String imageUrl, Boolean isMain) {
            this.bikeListing = bikeListing;
            this.imageType = imageType;
            this.imageUrl = imageUrl;
            this.isMain = isMain;
        }

        @PrePersist
        protected void onCreate() {
            this.uploadedAt = LocalDateTime.now();
        }

        // ===== Getter & Setter =====

        public Integer getImageId() {
            return imageId;
        }

        public void setImageId(Integer imageId) {
            this.imageId = imageId;
        }

        public BikeListings getBikeListing() {
            return bikeListing;
        }

        public void setBikeListing(BikeListings bikeListing) {
            this.bikeListing = bikeListing;
        }

        public String getImageType() {
            return imageType;
        }

        public void setImageType(String imageType) {
            this.imageType = imageType;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public Boolean getIsMain() {
            return isMain;
        }

        public void setIsMain(Boolean isMain) {
            this.isMain = isMain;
        }

        public LocalDateTime getUploadedAt() {
            return uploadedAt;
        }


}
