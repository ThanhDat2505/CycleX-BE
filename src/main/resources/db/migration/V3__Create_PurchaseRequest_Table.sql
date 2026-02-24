-- V3__Create_PurchaseRequest_Table.sql
-- Migration for S-50: Purchase Request feature

CREATE TABLE IF NOT EXISTS purchase_requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Purchase request ID',
    listing_id INT NOT NULL COMMENT 'Reference to bike listing',
    buyer_id INT NOT NULL COMMENT 'Reference to buyer user',
    transaction_type VARCHAR(20) NOT NULL COMMENT 'PURCHASE or DEPOSIT',
    desired_transaction_time DATETIME NOT NULL COMMENT 'Buyer desired transaction time',
    note TEXT COMMENT 'Optional notes from buyer (max 500 chars)',
    deposit_amount DECIMAL(15, 2) NOT NULL DEFAULT 0 COMMENT '10% of listing price',
    platform_fee DECIMAL(15, 2) NOT NULL DEFAULT 0 COMMENT 'Platform fee (5% of listing price)',
    inspection_fee DECIMAL(15, 2) NOT NULL DEFAULT 0 COMMENT 'Inspection fee (3% of listing price)',
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_SELLER_CONFIRM' COMMENT 'Request status',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',

    CONSTRAINT fk_purchase_request_listing FOREIGN KEY (listing_id)
        REFERENCES bike_listings(listing_id) ON DELETE RESTRICT,
    CONSTRAINT fk_purchase_request_buyer FOREIGN KEY (buyer_id)
        REFERENCES users(user_id) ON DELETE RESTRICT,

    INDEX idx_listing_id (listing_id),
    INDEX idx_buyer_id (buyer_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Purchase requests created by buyers for bike listings';

