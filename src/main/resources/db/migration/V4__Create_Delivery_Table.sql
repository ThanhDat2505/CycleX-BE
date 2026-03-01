-- V4__Create_Delivery_Table.sql
-- Migration for Shipper Delivery Management Feature

CREATE TABLE IF NOT EXISTS deliveries (
    delivery_id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Delivery ID',
    shipper_id INT NOT NULL COMMENT 'Reference to shipper user',
    transaction_id INT NOT NULL COMMENT 'Reference to purchase request (transaction)',
    listing_id INT NOT NULL COMMENT 'Reference to bike listing',
    pickup_address TEXT NOT NULL COMMENT 'Pickup address for the delivery',
    dropoff_address TEXT NOT NULL COMMENT 'Dropoff address for the delivery',
    status VARCHAR(30) NOT NULL DEFAULT 'ASSIGNED' COMMENT 'Delivery status (ASSIGNED, IN_PROGRESS, COMPLETED, FAILED, CANCELLED)',
    notes TEXT COMMENT 'Optional notes about the delivery',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',

    CONSTRAINT fk_delivery_shipper FOREIGN KEY (shipper_id)
        REFERENCES users(user_id) ON DELETE RESTRICT,
    CONSTRAINT fk_delivery_transaction FOREIGN KEY (transaction_id)
        REFERENCES purchase_requests(request_id) ON DELETE RESTRICT,
    CONSTRAINT fk_delivery_listing FOREIGN KEY (listing_id)
        REFERENCES bike_listings(listing_id) ON DELETE RESTRICT,

    INDEX idx_shipper_id (shipper_id),
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_listing_id (listing_id),
    INDEX idx_status (status),
    INDEX idx_shipper_status (shipper_id, status),
    INDEX idx_updated_at (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Deliveries assigned to shippers for completing bike transactions';

