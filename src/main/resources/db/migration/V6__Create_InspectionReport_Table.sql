CREATE TABLE inspection_reports (
    report_id INT AUTO_INCREMENT PRIMARY KEY,
    listing_id INT NOT NULL,
    inspector_id INT NOT NULL,
    decision VARCHAR(20) NOT NULL COMMENT 'APPROVED or REJECTED',
    reason_code VARCHAR(50) COMMENT 'e.g. MEETS_STANDARDS, DUPLICATE, INVALID_INFO, LOW_QUALITY, INAPPROPRIATE, OTHER',
    reason_text TEXT NOT NULL COMMENT 'Detailed reason for the decision',
    note TEXT COMMENT 'Optional internal note from inspector',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (listing_id) REFERENCES bike_listings(listing_id),
    FOREIGN KEY (inspector_id) REFERENCES users(user_id)
);

-- Index for fast lookup by listing
CREATE INDEX idx_inspection_reports_listing ON inspection_reports(listing_id);

-- Index for fast lookup by inspector
CREATE INDEX idx_inspection_reports_inspector ON inspection_reports(inspector_id);
