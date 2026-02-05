-- Flyway migration: V2__Create_listing_images_table.sql
CREATE TABLE listing_images (
    image_id SERIAL PRIMARY KEY,
    listing_id INTEGER NOT NULL,
    image_path VARCHAR(500) NOT NULL,
    image_order INTEGER NOT NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_listing_images_listing FOREIGN KEY (listing_id)
        REFERENCES bike_listings(listing_id) ON DELETE CASCADE,

    INDEX idx_listing_images_listing (listing_id),
    INDEX idx_listing_images_order (listing_id, image_order)
);

-- Add comment to table
COMMENT ON TABLE listing_images IS 'Lưu thông tin ảnh của listing (path dẫn tới FE public folder)';
COMMENT ON COLUMN listing_images.image_path IS 'Path: /public/{listingId}/number.jpg';
COMMENT ON COLUMN listing_images.image_order IS 'Thứ tự ảnh: 1, 2, 3...';
