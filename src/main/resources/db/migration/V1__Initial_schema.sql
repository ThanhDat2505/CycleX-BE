-- Flyway migration: V1__Initial_schema.sql
-- Create initial tables for CycleX-BE application

CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    cccd VARCHAR(12) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(30) NOT NULL DEFAULT 'SELLER',
    password_hash VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CHECK (role IN ('SELLER', 'INSPECTOR', 'ADMIN'))
);

CREATE TABLE email_otps (
    otp_id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    otp_code VARCHAR(6) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE bike_listings (
    listing_id SERIAL PRIMARY KEY,
    seller_id INTEGER NOT NULL,
    inspector_id INTEGER,
    title VARCHAR(255),
    description TEXT,
    bike_type VARCHAR(50),
    brand VARCHAR(100),
    model VARCHAR(100),
    manufacture_year INTEGER,
    condition VARCHAR(50),
    usage_time VARCHAR(100),
    reason_for_sale TEXT,
    price DECIMAL(15, 2),
    location_city VARCHAR(100),
    pickup_address TEXT,
    status VARCHAR(30) DEFAULT 'PENDING',
    views_count INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_bike_listings_seller FOREIGN KEY (seller_id)
        REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_bike_listings_inspector FOREIGN KEY (inspector_id)
        REFERENCES users(user_id) ON DELETE SET NULL,
    CHECK (status IN ('DRAFT', 'PENDING', 'REVIEWING', 'APPROVED', 'REJECTED', 'SOLD', 'DELISTED'))
);

-- Create indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_cccd ON users(cccd);
CREATE INDEX idx_bike_listings_seller ON bike_listings(seller_id);
CREATE INDEX idx_bike_listings_inspector ON bike_listings(inspector_id);
CREATE INDEX idx_bike_listings_status ON bike_listings(status);
CREATE INDEX idx_bike_listings_created_at ON bike_listings(created_at);
