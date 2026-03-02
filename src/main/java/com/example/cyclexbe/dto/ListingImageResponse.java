package com.example.cyclexbe.dto;

import com.example.cyclexbe.entity.ListingImage;
import java.time.LocalDateTime;

public class ListingImageResponse {
    public Integer imageId;
    public String imagePath;
    public Integer imageOrder;
    public LocalDateTime uploadedAt;

    public ListingImageResponse() {}

    public ListingImageResponse(Integer imageId, String imagePath, Integer imageOrder, LocalDateTime uploadedAt) {
        this.imageId = imageId;
        this.imagePath = imagePath;
        this.imageOrder = imageOrder;
        this.uploadedAt = uploadedAt;
    }

    public static ListingImageResponse from(ListingImage image) {
        return new ListingImageResponse(
                image.getImageId(),
                image.getImagePath(),
                image.getImageOrder(),
                image.getUploadedAt()
        );
    }
}
