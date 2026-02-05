package com.example.cyclexbe.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "listing_images")
public class ListingImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private BikeListing bikeListing;

    @Column(name = "image_path", nullable = false, length = 500)
    private String imagePath; // Lưu path: /public/{listingId}/1.jpg

    @Column(name = "image_order", nullable = false)
    private Integer imageOrder; // Thứ tự ảnh: 1, 2, 3...

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    public ListingImage() {}

    public ListingImage(BikeListing bikeListing, String imagePath, Integer imageOrder) {
        this.bikeListing = bikeListing;
        this.imagePath = imagePath;
        this.imageOrder = imageOrder;
    }

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
    }

    // Getters & Setters
    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public BikeListing getBikeListing() {
        return bikeListing;
    }

    public void setBikeListing(BikeListing bikeListing) {
        this.bikeListing = bikeListing;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Integer getImageOrder() {
        return imageOrder;
    }

    public void setImageOrder(Integer imageOrder) {
        this.imageOrder = imageOrder;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
