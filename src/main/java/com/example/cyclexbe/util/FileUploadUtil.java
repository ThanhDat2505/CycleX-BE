package com.example.cyclexbe.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * FileUploadUtil - Utility để xử lý upload file
 */
@Component
public class FileUploadUtil {

    @Value("${file.upload.dir:uploads/chat}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_MIME_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp"};

    /**
     * Validate và upload file ảnh
     * @param file File được upload
     * @return Relative path của file đã lưu
     * @throws IOException nếu có lỗi upload
     * @throws IllegalArgumentException nếu file không hợp lệ
     */
    public String uploadImage(MultipartFile file) throws IOException {
        // Validate file không null
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }

        // Validate size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 5MB limit");
        }

        // Validate MIME type
        if (!isValidImageMimeType(file.getContentType())) {
            throw new IllegalArgumentException("Invalid file type. Only images are allowed");
        }

        // Create upload directory if not exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String filename = UUID.randomUUID() + "-" + sanitizeFilename(file.getOriginalFilename());
        Path filePath = uploadPath.resolve(filename);

        // Save file
        Files.write(filePath, file.getBytes());

        // Return relative path (for storage in DB)
        return uploadDir + "/" + filename;
    }

    /**
     * Check if MIME type is a valid image
     */
    private boolean isValidImageMimeType(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        for (String allowed : ALLOWED_MIME_TYPES) {
            if (mimeType.equals(allowed)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sanitize filename to prevent path traversal
     */
    private String sanitizeFilename(String filename) {
        if (filename == null) {
            return "unknown";
        }
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    /**
     * Delete file
     */
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
            }
        } catch (IOException e) {
            // Log error but don't throw - file deletion is non-critical
            System.err.println("Failed to delete file: " + filePath);
        }
    }
}

