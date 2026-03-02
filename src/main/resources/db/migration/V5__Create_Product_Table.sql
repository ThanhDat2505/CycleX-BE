CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    listing_id INT NOT NULL UNIQUE,
    seller_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(15, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (listing_id) REFERENCES bike_listings(listing_id),
    FOREIGN KEY (seller_id) REFERENCES users(user_id)
);

ALTER TABLE purchase_requests ADD COLUMN product_id INT;
ALTER TABLE purchase_requests ADD CONSTRAINT fk_purchase_requests_product FOREIGN KEY (product_id) REFERENCES products(product_id);

-- Optional: Migrate existing approved listings to products
INSERT INTO products (listing_id, seller_id, name, description, price, status, created_at, updated_at)
SELECT listing_id, seller_id, title, description, price, 'AVAILABLE', created_at, updated_at
FROM bike_listings
WHERE status = 'APPROVED';

-- Update existing purchase requests to link to the newly created products based on listing_id
UPDATE purchase_requests pr
JOIN products p ON pr.listing_id = p.listing_id
SET pr.product_id = p.product_id;

-- Make product_id NOT NULL if you want to enforce it for future, but listing_id might still be kept for history or removed.
-- For now, we will keep listing_id nullable or remove it. Let's make listing_id nullable in purchase_requests
ALTER TABLE purchase_requests MODIFY COLUMN listing_id INT NULL;
