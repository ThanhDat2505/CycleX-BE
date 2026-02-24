-- Migration for S-50: Purchase Request Feature
-- Creates transaction_requests table to store buyer purchase requests

CREATE TABLE IF NOT EXISTS transaction_requests (
    transaction_id SERIAL PRIMARY KEY,
    listing_id INTEGER NOT NULL,
    buyer_id INTEGER NOT NULL,
    seller_id INTEGER NOT NULL,
    transaction_type VARCHAR(30) NOT NULL, -- PURCHASE, DEPOSIT
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_SELLER_CONFIRM',
    desired_time TIMESTAMP NOT NULL,
    note TEXT,
    amount DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (listing_id) REFERENCES bike_listings(listing_id) ON DELETE RESTRICT,
    FOREIGN KEY (buyer_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    FOREIGN KEY (seller_id) REFERENCES users(user_id) ON DELETE RESTRICT,

    -- Indexes for common queries
    INDEX idx_buyer_status (buyer_id, status),
    INDEX idx_listing_status (listing_id, status),
    INDEX idx_seller_id (seller_id),
    INDEX idx_created_at (created_at)
);

-- Unique constraint to prevent duplicate pending requests
-- (same buyer + same listing cannot have multiple PENDING_SELLER_CONFIRM requests)
ALTER TABLE transaction_requests
ADD CONSTRAINT unique_pending_request
UNIQUE (buyer_id, listing_id, status);

-- Note: If your database uses PostgreSQL, adjust syntax:
-- Use BIGSERIAL instead of SERIAL for transaction_id
-- Use CONSTRAINT syntax for indexes
-- Example for PostgreSQL:
/*
CREATE TABLE IF NOT EXISTS transaction_requests (
    transaction_id BIGSERIAL PRIMARY KEY,
    listing_id BIGINT NOT NULL,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_SELLER_CONFIRM',
    desired_time TIMESTAMP NOT NULL,
    note TEXT,
    amount DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (listing_id) REFERENCES bike_listings(listing_id) ON DELETE RESTRICT,
    FOREIGN KEY (buyer_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    FOREIGN KEY (seller_id) REFERENCES users(user_id) ON DELETE RESTRICT
);

CREATE INDEX idx_transaction_buyer_status ON transaction_requests(buyer_id, status);
CREATE INDEX idx_transaction_listing_status ON transaction_requests(listing_id, status);
CREATE INDEX idx_transaction_seller ON transaction_requests(seller_id);
CREATE INDEX idx_transaction_created ON transaction_requests(created_at);

ALTER TABLE transaction_requests
ADD CONSTRAINT unique_pending_request
UNIQUE (buyer_id, listing_id, status);
*/

