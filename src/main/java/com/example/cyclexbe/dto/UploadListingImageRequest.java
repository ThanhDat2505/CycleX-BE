package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotBlank;

public class UploadListingImageRequest {
    @NotBlank(message = "Image path is required")
    public String imagePath; // Path từ FE: /public/{listingId}/1.jpg

    public UploadListingImageRequest() {}

    public UploadListingImageRequest(String imagePath) {
        this.imagePath = imagePath;
    }
}
