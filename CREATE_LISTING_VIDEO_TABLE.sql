-- Create listing_videos table (1 video per listing)
CREATE TABLE IF NOT EXISTS listing_videos (
    video_id INT AUTO_INCREMENT PRIMARY KEY,
    listing_id INT NOT NULL UNIQUE,
    video_path VARCHAR(500) NOT NULL,
    uploaded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_listing_video_listing FOREIGN KEY (listing_id) REFERENCES bike_listings(listing_id) ON DELETE CASCADE
);
