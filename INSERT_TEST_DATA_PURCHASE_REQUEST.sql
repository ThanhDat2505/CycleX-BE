-- ============================================================
-- TEST DATA FOR PURCHASE REQUEST - S-50
-- ============================================================
-- Script này tạo dữ liệu test để bạn có thể kiểm tra Purchase Request API trên Postman
--
-- Data được tạo:
-- 1. User 1 (Seller): ID = 100
-- 2. User 2 (Buyer): ID = 101
-- 3. Bike Listing: ID = 50
-- 4. Product: ID = 10
-- 5. Purchase Request: ID = 5
-- ============================================================

-- 1. CREATE TEST SELLER
INSERT INTO users (user_id, cccd, full_name, email, phone, role, password_hash, is_verify, status, created_at, updated_at)
VALUES (
    100,
    '123456789999',
    'Nguyen Van Seller',
    'seller.test@example.com',
    '0987654321',
    'SELLER',
    '$2a$10$dXJ3SW6G7P50eS3HqSz7zODHC1owWsnwhddGHv2X.WdQY6P0Q.D3u', -- password: password123
    TRUE,
    'ACTIVE',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE email=VALUES(email);

-- 2. CREATE TEST BUYER
INSERT INTO users (user_id, cccd, full_name, email, phone, role, password_hash, is_verify, status, created_at, updated_at)
VALUES (
    101,
    '987654321999',
    'Tran Thi Buyer',
    'buyer.test@example.com',
    '0912345678',
    'BUYER',
    '$2a$10$dXJ3SW6G7P50eS3HqSz7zODHC1owWsnwhddGHv2X.WdQY6P0Q.D3u', -- password: password123
    TRUE,
    'ACTIVE',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE email=VALUES(email);

-- 3. CREATE TEST BIKE LISTING (APPROVED)
INSERT INTO bike_listings (
    listing_id,
    seller_id,
    title,
    description,
    bike_type,
    brand,
    model,
    manufacture_year,
    condition,
    usage_time,
    reason_for_sale,
    price,
    location_city,
    pickup_address,
    status,
    views_count,
    created_at,
    updated_at
) VALUES (
    50,
    100,
    'Yamaha YZF-R1 2023 - Sports Bike',
    'Beautiful sports bike in excellent condition. Low mileage, one owner.',
    'SPORT',
    'Yamaha',
    'YZF-R1',
    2023,
    'EXCELLENT',
    '5000 km',
    'Upgrading to a new model',
    15000.00,
    'Ho Chi Minh',
    '123 Nguyen Hue, District 1, Ho Chi Minh',
    'APPROVED',
    150,
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE status=VALUES(status);

-- 4. CREATE TEST PRODUCT (from the listing)
INSERT INTO products (product_id, listing_id, seller_id, name, description, price, status, created_at, updated_at)
VALUES (
    10,
    50,
    100,
    'Yamaha YZF-R1 2023 - Sports Bike',
    'Beautiful sports bike in excellent condition. Low mileage, one owner.',
    15000.00,
    'AVAILABLE',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE status=VALUES(status);

-- 5. CREATE TEST PURCHASE REQUEST
INSERT INTO purchase_requests (
    request_id,
    product_id,
    listing_id,
    buyer_id,
    transaction_type,
    desired_transaction_time,
    note,
    deposit_amount,
    platform_fee,
    inspection_fee,
    status,
    created_at,
    updated_at
) VALUES (
    5,
    10,
    50,
    101,
    'PURCHASE',
    DATE_ADD(NOW(), INTERVAL 5 DAY),
    'I am very interested in this bike. Ready to inspect and pay.',
    1500.00,  -- 10% of 15000
    750.00,   -- 5% of 15000
    450.00,   -- 3% of 15000
    'PENDING_SELLER_CONFIRM',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE status=VALUES(status);

-- ============================================================
-- TEST DATA FOR ADDITIONAL SCENARIOS
-- ============================================================

-- SCENARIO 2: Another product with different price
INSERT INTO bike_listings (
    listing_id,
    seller_id,
    title,
    description,
    bike_type,
    brand,
    model,
    manufacture_year,
    condition,
    usage_time,
    reason_for_sale,
    price,
    location_city,
    pickup_address,
    status,
    views_count,
    created_at,
    updated_at
) VALUES (
    51,
    100,
    'Honda CB500F 2022 - Naked Bike',
    'Reliable naked bike, perfect for daily commute and weekend rides.',
    'NAKED',
    'Honda',
    'CB500F',
    2022,
    'VERY_GOOD',
    '12000 km',
    'Need space for new bike',
    8500.00,
    'Hanoi',
    '456 Le Loi, Ba Dinh, Hanoi',
    'APPROVED',
    95,
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE status=VALUES(status);

INSERT INTO products (product_id, listing_id, seller_id, name, description, price, status, created_at, updated_at)
VALUES (
    11,
    51,
    100,
    'Honda CB500F 2022 - Naked Bike',
    'Reliable naked bike, perfect for daily commute and weekend rides.',
    8500.00,
    'AVAILABLE',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE status=VALUES(status);

-- SCENARIO 3: Purchase Request with different status
INSERT INTO purchase_requests (
    request_id,
    product_id,
    listing_id,
    buyer_id,
    transaction_type,
    desired_transaction_time,
    note,
    deposit_amount,
    platform_fee,
    inspection_fee,
    status,
    created_at,
    updated_at
) VALUES (
    6,
    11,
    51,
    101,
    'PURCHASE',
    DATE_ADD(NOW(), INTERVAL 3 DAY),
    'Interested in this Honda bike. Looking forward to inspect.',
    850.00,   -- 10% of 8500
    425.00,   -- 5% of 8500
    255.00,   -- 3% of 8500
    'PENDING_SELLER_CONFIRM',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE status=VALUES(status);

-- ============================================================
-- SUMMARY OF TEST DATA
-- ============================================================
-- Seller: seller.test@example.com (user_id: 100)
--         Password: password123
--
-- Buyer:  buyer.test@example.com (user_id: 101)
--         Password: password123
--
-- Products:
--   1. Yamaha YZF-R1 2023 (product_id: 10, listing_id: 50)
--      Price: 15000.00
--      Status: APPROVED
--
--   2. Honda CB500F 2022 (product_id: 11, listing_id: 51)
--      Price: 8500.00
--      Status: APPROVED
--
-- Purchase Requests:
--   1. Request ID 5: Product 10 (Yamaha) by Buyer 101
--      Status: PENDING_SELLER_CONFIRM
--
--   2. Request ID 6: Product 11 (Honda) by Buyer 101
--      Status: PENDING_SELLER_CONFIRM
-- ============================================================

-- Display inserted data
SELECT '=== USERS ===' as 'Data Summary';
SELECT user_id, full_name, email, role FROM users WHERE user_id IN (100, 101);

SELECT '=== BIKE LISTINGS ===' as 'Data Summary';
SELECT listing_id, title, brand, model, price, status FROM bike_listings WHERE listing_id IN (50, 51);

SELECT '=== PRODUCTS ===' as 'Data Summary';
SELECT product_id, listing_id, name, price, status FROM products WHERE product_id IN (10, 11);

SELECT '=== PURCHASE REQUESTS ===' as 'Data Summary';
SELECT request_id, product_id, buyer_id, transaction_type, status, deposit_amount, platform_fee, inspection_fee
FROM purchase_requests WHERE request_id IN (5, 6);

